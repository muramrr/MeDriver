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

package com.mmdev.me.driver.data.repository.settings

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.user.auth.IFirebaseAuthDataSource
import com.mmdev.me.driver.data.sync.download.IDataDownloader
import com.mmdev.me.driver.domain.core.ResultState.Companion.toUnit
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.ISettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [ISettingsRepository] implementation
 */

class SettingsRepositoryImpl(
	private val authDataSource: IFirebaseAuthDataSource,
	private val dataDownloader: IDataDownloader
): BaseRepository(), ISettingsRepository {
	
	
	override fun resetPassword(email: String): Flow<SimpleResult<Unit>> =
		authDataSource.resetPassword(email).map { it.toUnit() }
	
	
	override fun sendEmailVerification(email: String): Flow<SimpleResult<Unit>> =
		authDataSource.sendEmailVerification(email).map { it.toUnit() }
	
	
	override fun signIn(email: String, password: String): Flow<SimpleResult<Unit>> =
		authDataSource.signIn(email, password).map {
			if (MedriverApp.savedUserEmail.isNotBlank() && MedriverApp.savedUserEmail != email)
				dataDownloader.deleteAll()
			it.toUnit()
		}
	
	override fun signOut() = authDataSource.signOut()
	
	override fun signUp(email: String, password: String): Flow<SimpleResult<Unit>> =
		authDataSource.signUp(email, password).map {
			if (MedriverApp.savedUserEmail.isNotBlank() && MedriverApp.savedUserEmail != email)
				dataDownloader.deleteAll()
			it.toUnit()
		}

}