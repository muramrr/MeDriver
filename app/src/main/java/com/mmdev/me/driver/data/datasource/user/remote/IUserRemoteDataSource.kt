/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 03:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.remote

import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDTO
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserModel
import kotlinx.coroutines.flow.Flow

/**
 * Used to write and get [UserModel] object from backend while sing in/up
 */

interface IUserRemoteDataSource {
	
	fun getFirestoreUser(email: String): Flow<SimpleResult<FirestoreUserDTO>>
	
	fun updateFirestoreUserField(
		email: String, field: String, value: Any
	): Flow<SimpleResult<Void>>
	
	fun writeFirestoreUser(userDTOBackend: FirestoreUserDTO): Flow<SimpleResult<Unit>>
}