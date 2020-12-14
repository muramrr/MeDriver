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

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.installations.FirebaseInstallations
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.mapToDomainUserData
import com.mmdev.me.driver.data.datasource.billing.BillingDataSource
import com.mmdev.me.driver.data.datasource.user.auth.AuthCollector
import com.mmdev.me.driver.data.datasource.user.remote.IUserRemoteDataSource
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.data.repository.auth.mappers.UserMappers
import com.mmdev.me.driver.domain.billing.SubscriptionType.FREE
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
	private val billingDataSource: BillingDataSource,
	private val mappers: UserMappers
): IAuthFlowProvider {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	private companion object{
		private const val IS_EMAIL_VERIFIED_FIELD = "isEmailVerified"
		private const val INSTALLATION_TOKENS_FIELD = "installationTokens"
		
		private const val PURCHASES_FIELD = "purchases"
	}
	
	private val firebaseUserFlow: Flow<FirebaseUser?> = authCollector.firebaseAuthFlow.map {
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
	override fun getAuthUserFlow() = firebaseUserFlow.transform { firebaseUser ->
			
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
					
					updateDeviceToken(firestoreUser)
					
					/**
					 * Checks is user email verified
					 * This value generally comes from [FirebaseUser] object
					 * If emailVerification differs from de-serialized from backend [FirestoreUserDto]
					 * then invokes [updateEmailVerification] method
					 */
					if (firestoreUser.isEmailVerified != firebaseUser.isEmailVerified) {
						
						logWarn(TAG, "Email verification status needs update.")
						
						updateEmailVerification(firestoreUser, firebaseUser).collect {
							emit(it)
						}
						
					}
					// if emailVerified values are same -> convert retrieved firestoreUser
					else {
						logInfo(TAG, "Email verification status is up to date...")
						
						//purchases can exists only when user verifies email
						billingDataSource.purchases.collect { purchases ->
							logDebug(TAG, "Collected purchases: $purchases")
							emit(mappers.dtoToDomain(
								firestoreUser,
								if (!purchases.isNullOrEmpty()) mappers.parseSku(purchases.first().sku)
								else FREE
							))
						}
						
					}
				},
				
				/**
				 * user info doesn't exist on backend or other error was thrown
				 * probably, we have SIGN_UP case, so convert [FirebaseUser]
				 */
				failure = { error ->
					logError(TAG, "Failed to retrieve user info from backend... ${error.message}")
					
					firebaseUser.sendEmailVerification() // send email verification
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
	 * Rewrite field value and save it local, also emit user with updated field
	 */
	private fun updateEmailVerification(
		firestoreUserDto: FirestoreUserDto,
		firebaseUser: FirebaseUser
	): Flow<UserDataInfo> = userRemoteDataSource.updateFirestoreUserField(
		email = firestoreUserDto.email,
		field = IS_EMAIL_VERIFIED_FIELD,
		value = firebaseUser.isEmailVerified
	).map { updateResult ->
			
		logDebug(TAG, "Trying to update email verification...")
			
		updateResult.fold(
			success = {
				
				logInfo(TAG, "Email status updated...")
				val updatedUser = firestoreUserDto.copy(
					isEmailVerified = firebaseUser.isEmailVerified
				)
				
				mappers.dtoToDomain(updatedUser)
			},
			failure = {
				
				logError(TAG, "Failed to update email status... ${it.message}")
				
				//emit old firestoreUser object
				//without additional "get" request
				mappers.dtoToDomain(firestoreUserDto)
			}
		)
	}
	
	
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
	
	override fun purchaseFlow(activity: Activity, skuIdentifier: String, accountId: String) =
		billingDataSource.launchBillingFlow(activity, skuIdentifier, accountId)
		
	override fun observeNewPurchases(email: String) = billingDataSource.acknowledgedPurchase.transform { purchase ->
		val purchaseDto = mappers.toPurchaseDto(purchase).copy(isAcknowledged = true)
		userRemoteDataSource.updateFirestoreUserField(
			email,
			PURCHASES_FIELD,
			FieldValue.arrayUnion(purchaseDto)
		).collect { result ->
			emit(purchaseDto.sku.subscriptionType)
			logInfo(TAG, "User update result = $result")
		}
	}
	
//	private fun updateUser(user: UserDataInfo): Flow<SimpleResult<Unit>> =
//		userRemoteDataSource.writeFirestoreUser(mappers.domainToDto(user))
	
	
}