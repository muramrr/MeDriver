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

package com.mmdev.me.driver.data.datasource.user.remote

import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserDataInfo
import kotlinx.coroutines.flow.Flow

/**
 * Used to write and get [UserDataInfo] object from backend while sing in/up
 */

interface IUserRemoteDataSource {
	
	/**
	 * Get user info stored at server
	 */
	fun getFirestoreUser(email: String): Flow<SimpleResult<FirestoreUserDto>>
	
	/**
	 * Get user info field stored at server
	 */
	fun updateFirestoreUserField(
		email: String, field: String, value: Any
	): Flow<SimpleResult<Void>>
	
	/**
	 * Write user info to the server
	 * Basically invokes when new user is signed up
	 */
	fun writeFirestoreUser(userDtoBackend: FirestoreUserDto): Flow<SimpleResult<Unit>>
}