/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.settings

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.user.auth.IFirebaseAuthDataSource
import com.mmdev.me.driver.domain.core.ResultState.Companion.toUnit
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.auth.ISettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [ISettingsRepository] implementation
 */

class SettingsRepositoryImpl(
	private val authDataSource: IFirebaseAuthDataSource
): ISettingsRepository, BaseRepository() {
	
	override fun resetPassword(email: String) : Flow<SimpleResult<Unit>> =
		authDataSource.resetPassword(email).map { it.toUnit() }
	
	
	override fun sendEmailVerification(email: String): Flow<SimpleResult<Unit>> =
		authDataSource.sendEmailVerification(email).map { it.toUnit() }
	
	
	override fun signIn(email: String, password: String): Flow<SimpleResult<Unit>> =
		authDataSource.signIn(email, password).map { it.toUnit() }
	
	
	override fun signOut() = authDataSource.signOut()
	
	override fun signUp(email: String, password: String): Flow<SimpleResult<Unit>> =
		authDataSource.signUp(email, password).map { it.toUnit() }

}