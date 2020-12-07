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