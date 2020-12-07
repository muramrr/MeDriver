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

package com.mmdev.me.driver.data.datasource.user.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * [IFirebaseAuthDataSource] implementation
 */

class FirebaseAuthDataSourceImpl(private val auth: FirebaseAuth) :
		IFirebaseAuthDataSource, BaseDataSource() {
	
	override fun resetPassword(email: String) : Flow<SimpleResult<Void>> = try {
		logInfo(TAG, "Sending password reset...")
		auth.sendPasswordResetEmail(email).asFlow()
	}
	catch (e: Exception) {
		logError(TAG, "PasswordReset Error: ${e.message}")
		flowOf(ResultState.failure(e))
	}
	
	
	
	override fun sendEmailVerification(email: String): Flow<SimpleResult<Void>> = try {
		auth.currentUser?.let { user ->
			logInfo(TAG, "Sending verification email...")
			user.sendEmailVerification().asFlow()
		} ?: flowOf(ResultState.failure(Exception("User is null")))
	}
	catch (e: Exception) {
		logError(TAG, "SendEmailVerification Error: ${e.message}")
		flowOf(ResultState.failure(e))
	}
	
	
	
	
	override fun signIn(email: String, password: String): Flow<SimpleResult<AuthResult>> = try {
		logInfo(TAG, "Signing in...")
		auth.signInWithEmailAndPassword(email, password).asFlow()
	}
	catch (e: Exception) {
		logError(TAG, "SignIn Error: ${e.message}")
		flowOf(ResultState.failure(e))
	}
	
	
	
	
	
	override fun signOut() = auth.signOut()
	
	override fun signUp(email: String, password: String): Flow<SimpleResult<AuthResult>> = try {
		logInfo(TAG, "Signing up...")
		auth.createUserWithEmailAndPassword(email, password).asFlow()
	} catch (e: Exception) {
		logError(TAG, "SignUp Error: ${e.message}")
		flowOf(ResultState.failure(e))
	}
	
}