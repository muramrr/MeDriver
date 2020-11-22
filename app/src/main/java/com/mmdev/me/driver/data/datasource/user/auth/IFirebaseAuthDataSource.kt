/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.auth

import com.google.firebase.auth.AuthResult
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper for [FirebaseAuth] user operations
 */

interface IFirebaseAuthDataSource {
	
	/**
	 * Send user an email with password reset link
	 */
	fun resetPassword(email: String): Flow<SimpleResult<Void>>
	
	/**
	 * Send user an email with account confirmation link
	 */
	fun sendEmailVerification(email: String): Flow<SimpleResult<Void>>
	
	/**
	 * Execute signing in flow
	 */
	fun signIn(email: String, password: String): Flow<SimpleResult<AuthResult>>
	
	/**
	 * Sign out user
	 */
	fun signOut()
	
	/**
	 * Sign up new user with given [email] and [password]
	 */
	fun signUp(email: String, password: String): Flow<SimpleResult<AuthResult>>
	
}