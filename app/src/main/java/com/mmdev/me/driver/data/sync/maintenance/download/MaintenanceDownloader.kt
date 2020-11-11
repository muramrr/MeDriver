/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.11.2020 16:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.maintenance.download

import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.remote.IMaintenanceRemoteDataSource
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IMaintenanceDownloader] implementation
 */

class MaintenanceDownloader(
	private val local: IMaintenanceLocalDataSource,
	private val server: IMaintenanceRemoteDataSource,
	private val mappers: MaintenanceMappersFacade
): IMaintenanceDownloader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun download(email: String, vin: String): Flow<SimpleResult<Unit>> = flow {
		server.getMaintenanceHistory(email, vin).collect { result ->
			result.fold(
				success = { emit(local.insertReplacedSpareParts(mappers.listDtoToEntities(it))) },
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	}
	
	override suspend fun clear(): SimpleResult<Unit> = local.clearAll()
	
}