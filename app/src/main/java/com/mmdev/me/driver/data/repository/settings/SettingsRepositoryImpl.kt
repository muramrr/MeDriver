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
import com.mmdev.me.driver.data.sync.download.DataDownloader
import com.mmdev.me.driver.domain.core.ResultState.Companion.toUnit
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.FetchingStatus
import com.mmdev.me.driver.domain.user.ISettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * [ISettingsRepository] implementation
 */

class SettingsRepositoryImpl(
	private val authDataSource: IFirebaseAuthDataSource,
	private val dataDownloader: DataDownloader
): BaseRepository(), ISettingsRepository {
	
	
	override fun downloadData(email: String): Flow<FetchingStatus> = flow {
		if (MedriverApp.savedUserEmail.isNotBlank() && MedriverApp.savedUserEmail != email) {
			emit(FetchingStatus.Deleting)
			dataDownloader.deleteAll()
		}
		delay(200)
		emit(FetchingStatus.Downloading)
		dataDownloader.importData(email).collect { result ->
			result.fold(
				success = { emit(FetchingStatus.Finished) },
				failure = { emit(FetchingStatus.Error) }
			)
		}
	}
	
	
	override fun resetPassword(email: String): Flow<SimpleResult<Unit>> =
		authDataSource.resetPassword(email).map { it.toUnit() }
	
	
	override fun sendEmailVerification(email: String): Flow<SimpleResult<Unit>> =
		authDataSource.sendEmailVerification(email).map { it.toUnit() }
	
	
	override fun signIn(email: String, password: String): Flow<SimpleResult<Unit>> =
		authDataSource.signIn(email, password).map { it.toUnit() }
	
	
	
	override fun signOut() = authDataSource.signOut()
	
	override fun signUp(email: String, password: String): Flow<SimpleResult<Unit>> =
		authDataSource.signUp(email, password).map { it.toUnit() }

}