/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.11.2020 17:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.upload.vehicle

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.remote.IVehicleRemoteDataSource
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
	private val server: IVehicleRemoteDataSource,
	private val mappers: VehicleMappersFacade
): IVehicleUploader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun fetch(email: String) = flow {
		logDebug(TAG, "getting vehicles cached operations...")
		local.getCachedOperations().fold(
			success = { operations ->
				logInfo(TAG, "vehicles cached operations count = ${operations.size}")
				operations.asFlow().flatMapMerge { operation ->
					logDebug(TAG, "executing $operation")
					flow {
						local.getVehicle(operation.recordId).fold(
							success = { vehicle ->
								server.addVehicle(
									email,
									mappers.entityToDto(vehicle)
								).collect { result ->
									result.fold(
										success = { emit(local.deleteCachedOperation(operation)) },
										failure = { emit(ResultState.failure(it)) }
									)
								}
							},
							failure = {
								emit(ResultState.failure(it))
							}
						)
					}
				}.collect { emit(it) }
			},
			failure = {
				emit(ResultState.failure(it))
			}
		)
	}
	
	
}