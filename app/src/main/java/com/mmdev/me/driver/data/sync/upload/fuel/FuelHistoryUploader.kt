/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.11.2020 17:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.upload.fuel

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.remote.IFuelHistoryRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * [IFuelHistoryUploader] implementation
 */

class FuelHistoryUploader(
	private val local: IFuelHistoryLocalDataSource,
	private val server: IFuelHistoryRemoteDataSource,
	private val mappers: FuelHistoryMappersFacade
): IFuelHistoryUploader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun fetch(email: String) = flow {
		logDebug(TAG, "getting fuel history cached operations...")
		local.getCachedOperations().fold(
			success = { operations ->
				logInfo(TAG, "fuel history cached operations count = ${operations.size}")
				operations.asFlow().flatMapMerge { operation ->
					logDebug(TAG, "executing $operation")
					flow {
						local.getRecordById(operation.recordId.toLong()).fold(
							success = { record ->
								if (record != null) {
									server.addFuelHistory(
										email,
										record.vehicleVinCode,
										mappers.entityToDto(record)
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
							failure = {
								emit(ResultState.failure(it))
							}
						)
					}
				}.collect { emit(it) }
			},
			failure = {
				logError(TAG, "${it.message}")
				emit(ResultState.failure(it))
			}
		)
	}
	
}