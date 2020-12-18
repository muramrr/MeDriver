/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.data.repository.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.installations.FirebaseInstallations
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.mapToDomainUserData
import com.mmdev.me.driver.data.datasource.user.auth.AuthCollector
import com.mmdev.me.driver.data.datasource.user.remote.IUserRemoteDataSource
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.data.repository.auth.mappers.UserMappers
import com.mmdev.me.driver.domain.user.AuthStatus
import com.mmdev.me.driver.domain.user.IAuthFlowProvider
import com.mmdev.me.driver.domain.user.UserDataInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

/**
 * Wrapper for [AuthCollector] provider
 * Main features:
 * convert [FirebaseAuth] callbacks to simplified [AuthStatus]
 * convert [FirebaseUser] which depend on [FirebaseAuth] callback and emit [UserDataInfo]
 */

class AuthFlowProviderImpl(
	authCollector: AuthCollector,
	private val userRemoteDataSource: IUserRemoteDataSource,
	private val mappers: UserMappers
): IAuthFlowProvider {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	private companion object{
		private const val IS_EMAIL_VERIFIED_FIELD = "isEmailVerified"
		private const val INSTALLATION_TOKENS_FIELD = "installationTokens"
	}
	
	private val authFlow: Flow<FirebaseUser?> = authCollector.firebaseAuthFlow.map {
		it.currentUser?.reload()
		it.currentUser
	}
	
	/**
	 * Very important and method
	 *
	 * First of all we are collecting flow result from [AuthCollector] emitted by AuthListener
	 * if result is not null then we are trying to get [FirestoreUserDto] from backend and check
	 * email verification (if not equals -> update field), also overwrite local user email whenever
	 * result from backend is and emit mapped retrieved user info
	 *
	 * if user info doesn't exists on backend -> write converted [FirebaseUser] and also emit it
	 * as a result
	 *
	 * keep in case that all of these scenarios triggered only when auth returns [firebaseUser != null]
	 */
	override fun getUserFlow(): Flow<UserDataInfo?> = authFlow.transform { firebaseUser ->
		
		logInfo(TAG, "Collecting auth information...")
		
		if (firebaseUser != null) {
			
			logInfo(TAG, "Auth info exists...")
			
			getUserFromRemoteStorage(firebaseUser).collect { emit(it) }
		}
		//not signed in
		else {
			logError(TAG, "Auth info does not exists...")
			
			emit(null)
		}
	}
	
	
	/**
	 * Trying to get user from firestore and update email if needed
	 * If failure -> convert given [FirebaseUser] and save it
	 */
	private fun getUserFromRemoteStorage(firebaseUser: FirebaseUser): Flow<UserDataInfo?> =
		userRemoteDataSource.getFirestoreUser(firebaseUser.email!!).transform { remoteUserState ->
			logDebug(TAG, "Retrieving user info from backend...")
			
			remoteUserState.fold(
				//user info exists on backend
				success = { firestoreUser ->
					logInfo(TAG, "User retrieved from backend, proceeding...")
					
					emit(mappers.dtoToDomain(firestoreUser, firebaseUser.isEmailVerified))
					
					updateDeviceToken(firestoreUser)
					updateEmailVerification(firestoreUser, firebaseUser)
					
				},
				
				/**
				 * user info doesn't exist on backend or other error was thrown
				 * probably, we have SIGN_UP case, so convert [FirebaseUser]
				 */
				failure = { error ->
					logError(TAG, "Failed to retrieve user info from backend... ${error.message}")
					
					firebaseUser.sendEmailVerification()
					logDebug(TAG, "Trying to write to backend...")
					userRemoteDataSource.writeFirestoreUser(mappers.firebaseUserToDto(firebaseUser)).collect { result ->
						result.fold(
							success = {
								logInfo(TAG, "User was saved to backend successfully")
								updateDeviceToken(mappers.firebaseUserToDto(firebaseUser))
								emit(firebaseUser.mapToDomainUserData() )
							},
							failure = {
								logError(TAG, "Can't save user to backend: ${it.message}")
								emit(null)
							}
						)
					}
					
				}
			)
		}
	
	/**
	 * Checks is user email verified
	 * This value generally comes from [FirebaseUser] object
	 * If emailVerification differs from de-serialized from backend [FirestoreUserDto]
	 * then invokes [updateEmailVerification] method
	 */
	private suspend fun updateEmailVerification(
		firestoreUser: FirestoreUserDto,
		firebaseUser: FirebaseUser
	) = if (firestoreUser.isEmailVerified != firebaseUser.isEmailVerified) {
		
		logWarn(TAG, "Email verification status needs update.")
		
		userRemoteDataSource.updateFirestoreUserField(
			email = firestoreUser.email,
			field = IS_EMAIL_VERIFIED_FIELD,
			value = firebaseUser.isEmailVerified
		).collect { updateResult ->
			
			logDebug(TAG, "Trying to update email verification...")
			
			updateResult.fold(
				success = { logInfo(TAG, "Email status updated...") },
				failure = { logError(TAG, "Failed to update email status... ${it.message}") }
			)
		}
		
	}
	else logInfo(TAG, "Email verification status is up to date...")
	
	
	private suspend fun updateDeviceToken(user: FirestoreUserDto) =
		FirebaseInstallations.getInstance().id.asFlow().collect { result ->
			result.fold(
				success = { token ->
					// if no token with such deviceId
					val updatedTokens = if (!user.installationTokens.containsKey(MedriverApp.androidId)) {
						user.installationTokens.plus(MedriverApp.androidId to token)
					}
					// update existing token
					else {
						user.installationTokens.minus(MedriverApp.androidId) // delete existing token
							.plus(MedriverApp.androidId to token) // add new token
						
					}
					userRemoteDataSource.updateFirestoreUserField(
						email = user.email,
						field = INSTALLATION_TOKENS_FIELD,
						value = updatedTokens
					).collect { tokenUpdateResult ->
						tokenUpdateResult.fold(
							success = { logInfo(TAG, "device token has been updated") },
							failure = { logError(TAG, "device token has NOT been updated")}
						)
					}
					
				},
				failure = {
					logError(TAG, "Token was not updated, ${it.message}")
				}
			)
			
		}
}