/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 18:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.local

import com.cioccarellia.ksprefs.KsPrefs
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * [IUserLocalDataSource] implementation
 */

internal class UserLocalDataSourceImpl (private val prefs: KsPrefs):
		IUserLocalDataSource, BaseDataSource() {
	
	companion object {
		private const val USER_RETRIEVE_KEY = "saved_user"
	}
	
	override fun getUser(): Flow<UserEntity?> = flow {
		try {
			val stringFromPrefs = prefs.pull<String>(USER_RETRIEVE_KEY)
			emit(Json.decodeFromString(UserEntity.serializer(), stringFromPrefs))
		}
		catch (e: Exception) {
			emit(null)
		}
	}
	
	
	
	override fun saveUser(userEntity: UserEntity): Flow<SimpleResult<Unit>> = flow {
		try {
			val stringifyEntity = Json.encodeToString(UserEntity.serializer(), userEntity)
			prefs.push(USER_RETRIEVE_KEY, stringifyEntity)
			emit(ResultState.success(Unit))
		}
		catch (e: Exception) {
			emit(ResultState.failure(e))
		}
	}
	
}