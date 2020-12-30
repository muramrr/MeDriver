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

package com.mmdev.me.driver.data.sync.upload.vehicle

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.server.IVehicleServerDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * [IVehicleUploader] implementation
 */

class VehicleUploader(
	private val local: IVehicleLocalDataSource,
	private val server: IVehicleServerDataSource,
	private val mappers: VehicleMappersFacade
): IVehicleUploader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun upload(email: String) {
		logDebug(TAG, "getting vehicles cached operations...")
		local.getCachedOperations().fold(
			success = { operations ->
				logInfo(TAG, "vehicles cached operations count = ${operations.size}")
				if (operations.isEmpty()) return
				operations.asFlow().flatMapMerge { operation ->
					logDebug(TAG, "executing $operation")
					flow {
						local.getVehicle(operation.recordId).fold(
							success = { vehicle ->
								if (vehicle != null) {
									server.addVehicle(
										email,
										mappers.entityToDto(vehicle)
									).collect { result ->
										result.fold(
											success = { emit(local.deleteCachedOperation(operation)) },
											failure = { emit(ResultState.failure(it)) }
										)
									}
								}
								else {
									logError(TAG, "No such record stored in database")
									emit(local.deleteCachedOperation(operation))
								}
							},
							failure = { emit(ResultState.failure(it)) }
						)
					}
				}.collect {
					logDebug(TAG, "Fetching vehicle history result = $it")
					//emit(it)
				}
			},
			failure = {
				logError(TAG, "${it.message}")
				//emit(ResultState.failure(it))
			}
		)
	}
	
	
}