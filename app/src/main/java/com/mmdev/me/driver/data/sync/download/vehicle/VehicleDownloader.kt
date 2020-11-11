/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.11.2020 18:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download.vehicle

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.remote.IVehicleRemoteDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * [IVehicleDownloader] implementation
 */

class VehicleDownloader(
	private val local: IVehicleLocalDataSource,
	private val server: IVehicleRemoteDataSource,
	private val mappers: VehicleMappersFacade
): IVehicleDownloader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun download(email: String) = flow {
		server.getAllVehicles(email).collect { result ->
			result.fold(
				success = { vehicles ->
					vehicles.asFlow().flatMapMerge { vehicleDto ->
						logDebug(TAG, "Inserting vehicle: ${vehicleDto.brand} ${vehicleDto.model}")
						flow {
							local.insertVehicle(mappers.dtoToEntity(vehicleDto)).fold(
								success = { emit(ResultState.success(vehicleDto.vin)) },
								failure = { emit(ResultState.failure(it)) }
							)
						}
					}.collect { emit(it) }
				},
				failure = { emit(ResultState.failure(it)) }
			)
		}
	}
	
	override suspend fun clear(): SimpleResult<Unit> = local.clearAll()
	
}