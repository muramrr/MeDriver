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

package com.mmdev.me.driver.data.datasource.user.local

import com.cioccarellia.ksprefs.KsPrefs
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.sync.download.DataDownloader
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * [IUserLocalDataSource] implementation
 */

class UserLocalDataSourceImpl(
	private val prefs: KsPrefs,
	private val dataDownloader: DataDownloader
): IUserLocalDataSource, BaseDataSource() {
	
	private companion object {
		private const val USER_RETRIEVE_KEY = "saved_user"
	}
	
	private fun getUser(): Flow<UserEntity?> = flow {
		try {
			val stringFromPrefs = prefs.pull(USER_RETRIEVE_KEY, "")
			if (stringFromPrefs.isNotEmpty())
				emit(Json.decodeFromString(UserEntity.serializer(), stringFromPrefs))
			else emit(null)
		}
		catch (e: Exception) {
			logError(TAG, "${e.message}")
			emit(null)
		}
	}
	
	override fun saveUser(userEntity: UserEntity): Flow<SimpleResult<Unit>> = flow {
		try {
			getUser().collect { savedUser ->
				if (savedUser != null) {
					
					if (savedUser.email != userEntity.email) {
						logWarn(TAG, "Email differs")
						//todo: find a better place to download data when user signs in
						dataDownloader.deleteAll()
						dataDownloader.importData(userEntity.email).collect {
							logInfo(TAG, "Downloading data result = $it")
						}
					}
				} else {
					dataDownloader.importData(userEntity.email).collect {
						logInfo(TAG, "Downloading data result = $it")
					}
				}
				
				
				val stringifyEntity = Json.encodeToString(UserEntity.serializer(), userEntity)
				
				prefs.push(USER_RETRIEVE_KEY, stringifyEntity)
				emit(ResultState.success(Unit))
				
			}
		}
		catch (e: Exception) {
			logError(TAG, "${e.message}")
			emit(ResultState.failure(e))
		}
	}
	
}