/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.09.2020 21:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.user.auth

import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Auth repository responsible for authentication and registration new users
 */

interface IAuthRepository {
	
	fun resetPassword(email: String) : Flow<SimpleResult<Unit>>
	
	fun sendEmailVerification(email: String) : Flow<SimpleResult<Unit>>
	
	fun signIn(email: String, password: String) : Flow<SimpleResult<Unit>>
	
	fun signOut()
	
	fun signUp(email: String, password: String) : Flow<SimpleResult<Unit>>
	
}