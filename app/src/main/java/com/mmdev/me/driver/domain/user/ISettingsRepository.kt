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

package com.mmdev.me.driver.domain.user

import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Settings repository responsible for account related operations
 */

interface ISettingsRepository {
	
	/**
	 * Send email for reset password if such user exists
	 */
	fun resetPassword(email: String): Flow<SimpleResult<Unit>>
	
	/**
	 * Send email for verification if such user exists
	 */
	fun sendEmailVerification(email: String): Flow<SimpleResult<Unit>>
	
	/**
	 * Basically just authenticate user
	 */
	fun signIn(email: String, password: String): Flow<SimpleResult<Unit>>
	
	/**
	 * Basically log out current user
	 */
	fun signOut()
	
	/**
	 * Try to sign up with given email and password
	 */
	fun signUp(email: String, password: String): Flow<SimpleResult<Unit>>
	
}