/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.11.2020 18:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.local

import com.cioccarellia.ksprefs.KsPrefs
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.DataManipulator
import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
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
	private val dataManipulator: DataManipulator
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
					
					if (savedUser.email != userEntity.email){
						logWtf(TAG, "Email differs")
						dataManipulator.deleteAll()
						dataManipulator.downloadData(userEntity.email).collect {
							logWtf(TAG, "$it")
						}
					}
				}
				else dataManipulator.downloadData(userEntity.email).collect {
					logWtf(TAG, "$it")
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