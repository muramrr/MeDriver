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

package com.mmdev.me.driver.data.sync.upload.fuel

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.server.IFuelHistoryServerDataSource
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
	private val server: IFuelHistoryServerDataSource,
	private val mappers: FuelHistoryMappersFacade
): IFuelHistoryUploader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun upload(email: String) {
		logDebug(TAG, "getting fuel history cached operations...")
		local.getCachedOperations().fold(
			success = { operations ->
				logInfo(TAG, "fuel history cached operations count = ${operations.size}")
				if (operations.isEmpty()) return
				operations.asFlow().flatMapMerge { operation ->
					logDebug(TAG, "executing $operation")
					flow {
						local.getRecordById(operation.recordId.toLong()).fold(
							success = { record ->
								if (record != null) {
									server.addFuelHistory(
										email,
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
							failure = { emit(ResultState.failure(it)) }
						)
					}
				}.collect {
					logDebug(TAG, "Fetching fuel history result = $it")
				}
			},
			failure = {
				logError(TAG, "${it.message}")
				//emit(ResultState.failure(it))
			}
		)
	}
	
}