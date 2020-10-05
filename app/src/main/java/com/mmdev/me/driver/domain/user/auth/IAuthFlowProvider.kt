/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.user.auth

import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserData
import kotlinx.coroutines.flow.Flow

/**
 * Main features:
 * convert [FirebaseAuth] callbacks to simplified [AuthStatus]
 * convert [FirebaseUser] which depend on [FirebaseAuth] callback and emit [UserData]
 */

interface IAuthFlowProvider {
	
	fun getAuthStatusFlow(): Flow<AuthStatus>
	
	fun getAuthUserFlow(): Flow<UserData?>
	
	fun updateUserModel(user: UserData): Flow<SimpleResult<Unit>>
}