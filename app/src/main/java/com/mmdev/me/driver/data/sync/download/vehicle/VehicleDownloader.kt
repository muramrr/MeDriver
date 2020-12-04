/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download.vehicle

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.server.IVehicleServerDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IVehicleDownloader] implementation
 */

class VehicleDownloader(
	private val local: IVehicleLocalDataSource,
	private val server: IVehicleServerDataSource,
	private val mappers: VehicleMappersFacade
): IVehicleDownloader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun download(email: String) = flow {
		logDebug(TAG, "Downloading vehicles...")
		server.getAllVehicles(email).collect { result ->
			result.fold(
				success = { vehicles ->
					local.importVehicles(mappers.listDtoToEntity(vehicles)).fold(
						success = {
							emit(ResultState.success(vehicles.map { it.vin }))
						},
						failure = {
							emit(ResultState.failure(it))
						}
					)
				},
				failure = { emit(ResultState.failure(it)) }
			)
		}
	}
	
	override suspend fun downloadSingle(
		email: String, vin: String
	): Flow<SimpleResult<Unit>> = flow {
		server.getVehicle(email, vin).collect { resultServer ->
			resultServer.fold(
				success = { local.importVehicles(listOf(mappers.dtoToEntity(it))) },
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	}
	
	override suspend fun clear(): SimpleResult<Unit> = local.clearAll()
	
}