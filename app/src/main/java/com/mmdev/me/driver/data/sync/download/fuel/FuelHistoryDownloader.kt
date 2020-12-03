/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 19:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download.fuel

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.remote.IFuelHistoryRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IFuelHistoryDownloader] implementation
 */

class FuelHistoryDownloader(
	private val local: IFuelHistoryLocalDataSource,
	private val server: IFuelHistoryRemoteDataSource,
	private val mappers: FuelHistoryMappersFacade
): IFuelHistoryDownloader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun download(email: String, vin: String): Flow<SimpleResult<Unit>> = flow {
		logDebug(TAG, "Downloading fuel history...")
		server.getAllFuelHistory(email, vin).collect { result ->
			result.fold(
				success = { emit(local.importFuelHistory(mappers.listDtoToEntities(it))) },
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	}
	
	override suspend fun downloadSingle(
		email: String, vin: String, id: String
	): Flow<SimpleResult<Unit>> = flow {
		server.getFuelHistoryById(email, vin, id).collect { resultServer ->
			resultServer.fold(
				success = { local.insertFuelHistoryEntry(mappers.dtoToEntity(it)) },
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	}
	
	override suspend fun clear(): SimpleResult<Unit> = local.clearAll()
	
}