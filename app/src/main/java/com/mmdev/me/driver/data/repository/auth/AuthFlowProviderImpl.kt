/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.09.2020 02:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.auth

import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.logError
import com.mmdev.me.driver.core.utils.logInfo
import com.mmdev.me.driver.core.utils.logWarn
import com.mmdev.me.driver.data.core.firebase.mapToUserModel
import com.mmdev.me.driver.data.datasource.user.auth.AuthCollector
import com.mmdev.me.driver.data.datasource.user.local.IUserLocalDataSource
import com.mmdev.me.driver.data.datasource.user.remote.IUserRemoteDataSource
import com.mmdev.me.driver.data.datasource.user.remote.model.FirestoreUser
import com.mmdev.me.driver.data.repository.auth.mappers.UserMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.user.auth.AuthStatus
import com.mmdev.me.driver.domain.user.auth.IAuthFlowProvider
import kotlinx.coroutines.flow.*

/**
 * Wrapper for [AuthCollector] provider
 * Main features:
 * convert [FirebaseAuth] callbacks to simplified [AuthStatus]
 * convert [FirebaseUser] which depend on [FirebaseAuth] callback and emit [UserModel]
 */

internal class AuthFlowProviderImpl (
	private val authCollector: AuthCollector,
	private val userLocalDataSource: IUserLocalDataSource,
	private val userRemoteDataSource: IUserRemoteDataSource,
	private val mappers: UserMappersFacade
) : IAuthFlowProvider {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	private companion object{
		private const val IS_EMAIL_VERIFIED_FIELD = "isEmailVerified"
	}
	
	private val firebaseUserFlow: Flow<FirebaseUser?> = flow {
		authCollector.firebaseAuthFlow.collect { auth ->
			auth.currentUser?.reload()
			emit(auth.currentUser)
		}
	}
	
	/** Collect AuthListener invocations and emit [AuthStatus] based on callback */
	override fun getAuthStatusFlow(): Flow<AuthStatus> = firebaseUserFlow.map {
		if (it != null) {
			//it.reload()
			AuthStatus.AUTHENTICATED
		}
		else AuthStatus.UNAUTHENTICATED
	}
	
	
	/**
	 * Very important and difficult method
	 *
	 * First of all we are collecting flow result from [AuthCollector] emitted by AuthListener
	 * if result is not null then we are trying to get [FirestoreUser] from backend and check
	 * email verification (if not equals -> update field), also overwrite local user info whenever
	 * result from backend is and emit mapped retrieved user info
	 *
	 * if user info doesn't exists on backend -> look for stored user at local storage and write
	 * to backend and also emit stored user at local storage as a result
	 *
	 * if user doesn't exists at local storage then its probably SIGN_UP case flow:
	 * write both to backend and local storage converted [FirebaseUser] and also emit it as a result
	 *
	 */
	override fun getAuthUserFlow(): Flow<UserModel?> = flow {
		firebaseUserFlow.collect { firebaseUser ->
			
			logInfo(TAG, "Collecting auth information...")
			
			if (firebaseUser != null) {
				
				logInfo(TAG, "Auth info exists...")
				
				getUserFromRemoteStorage(firebaseUser).collect {
					emit(it)
				}
			}
			//not signed in
			else {
				logError(TAG, "Auth info does not exists...")
				
				emit(null)
			}
		}
	}.flowOn(MyDispatchers.io())
	
	
	/**
	 * Trying to get user from firestore and update email if needed
	 * If failure -> try to get user from local storage
	 * @see [getUserFromLocalStorage]
	 */
	private fun getUserFromRemoteStorage(firebaseUser: FirebaseUser): Flow<UserModel?> = flow {
		logInfo(TAG, "Retrieving user info from backend...")
		
		userRemoteDataSource.getFirestoreUser(firebaseUser.email!!).collect { remoteUserState ->
			remoteUserState.fold(
				//user info exists on backend
				success = { firestoreUser ->
					logInfo(TAG, "User retrieved from backend, proceeding...")
					
					// check is email verified fetched
					checkEmailVerification(firestoreUser, firebaseUser).collect { emit(it) }
					
				},
				
				//user info doesn't exist on backend or other error was thrown
				failure = { error ->
					logError(TAG, "Failed to retrieve user info from backend... ${error.message}")
					
					//if document not exist on backend -> read from local and write to backend
					getUserFromLocalStorage(firebaseUser).collect {
						emit(it)
					}
					
				}
			)
		}
	}
	
	
	/**
	 * Trying to get user from local storage
	 * if failure -> convert [firebaseUser] and save both to backend and local
	 * Last case is probably invoked in signUp procession
	 */
	private fun getUserFromLocalStorage(firebaseUser: FirebaseUser) : Flow<UserModel?> = flow {
		
		logWarn(TAG, "Trying to get user from local storage...")
		
		userLocalDataSource.getUser().collect { cachedUser ->
			
			if (cachedUser != null) {
				
				logInfo(TAG, "User was found, writing to backend...")
				
				userRemoteDataSource.writeFirestoreUser(
					mappers.mapLocalUserToFirestoreUser(cachedUser)
				)
				
				//emit user info from local storage
				emit(mappers.mapLocalUserToDomainUser(cachedUser))
				
			}
			else {
				logWarn(TAG, "User wasn't found on local, " +
				             "trying to convert user provided by auth...")
				
				//combine both writing to backend and to local storage operations results
				userRemoteDataSource.writeFirestoreUser(
					mappers.mapFirebaseUserToFirestoreUser(firebaseUser)
				).combine(
					userLocalDataSource.saveUser(mappers.mapFirebaseUserToLocalUser(firebaseUser))
				) { writeRemote, writeLocal ->
					
					if (writeRemote is ResultState.Success &&
					    writeLocal is ResultState.Success){
						
						logInfo(TAG, "User was saved both to backend " +
						             "and local storage.")
						
						//return firebaseUser mapped to domain model
						firebaseUser.mapToUserModel()
						
					}
					else {
						logError(TAG, "Can't save firebase user. Magic...")
						
						//return null? what happened?
						null
					}
				}.collect { emit(it) } // collect combine result
			}
		}
	}
	
	
	
	/**
	 * Checks is user email verified
	 * This value generally comes from [FirebaseUser] object
	 * If emailVerification differs from de-serialized from backend [FirestoreUser]
	 * then invokes [updateEmailVerification] method
	 */
	private fun checkEmailVerification(
		firestoreUser: FirestoreUser,
		firebaseUser: FirebaseUser
	): Flow<UserModel> = flow {
		
		logInfo(TAG, "Checking email verification status...")
		
		if (firestoreUser.emailVerified != firebaseUser.isEmailVerified) {
			
			logWarn(TAG, "Email verification status needs update.")
			
			updateEmailVerification(firestoreUser, firebaseUser).collect {
				emit(it)
			}
			
		}
		// if emailVerified values are same -> convert retrieved firestoreUser
		else {
			logInfo(TAG, "Email verification status is up to date...")
			
			logInfo(TAG, "Fetching with local user info")
			userLocalDataSource.saveUser(
				mappers.mapFirestoreUserToLocalUser(firestoreUser)
			)
			
			emit(mappers.mapFirestoreUserToUserModel(firestoreUser))
		}
	}
	
	/**
	 * Rewrite field value and save it local, also emit mapped domain model with updated field
	 */
	private fun updateEmailVerification(
		firestoreUser: FirestoreUser,
		firebaseUser: FirebaseUser
	): Flow<UserModel> = flow {
		
		userRemoteDataSource.updateFirestoreUserField(
			email = firestoreUser.email,
			field = IS_EMAIL_VERIFIED_FIELD,
			value = firebaseUser.isEmailVerified
		).collect { updateResult ->
			
			logWarn(TAG, "Trying to update email verification...")
			
			updateResult.fold(
				success = {
					
					logInfo(TAG, "Email status updated...")
					val updatedUser = firestoreUser.copy(
						emailVerified = firebaseUser.isEmailVerified
					)
					
					//save updates to local storage
					userLocalDataSource.saveUser(
						mappers.mapFirestoreUserToLocalUser(updatedUser)
					)
					
					//emit updated firestoreUser object
					//without additional "get" request
					emit(mappers.mapFirestoreUserToUserModel(updatedUser))
				},
				failure = {
					
					logError(TAG, "Failed to update email status...")
					
					//emit old firestoreUser object
					//without additional "get" request
					emit(mappers.mapFirestoreUserToUserModel(firestoreUser))
				}
			)
		}
	}
	
	
}