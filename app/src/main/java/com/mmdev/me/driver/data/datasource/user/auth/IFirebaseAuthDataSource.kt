/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.09.2020 19:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Basically used to provide [FirebaseAuth] as flow
 */

interface IFirebaseAuthDataSource {
	
	fun resetPassword(email: String) : Flow<SimpleResult<Void>>
	
	fun sendEmailVerification(email: String) : Flow<SimpleResult<Void>>
	
	fun signIn(email: String, password: String) : Flow<SimpleResult<AuthResult>>
	
	fun signOut()
	
	fun signUp(email: String, password: String) : Flow<SimpleResult<AuthResult>>
	
}