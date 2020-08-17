/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.local.fuel.history.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord

/**
 * [IFuelHistoryRepository] implementation
 */

internal class FuelHistoryRepositoryImpl (
//	private val dataSourceRemote: RemoteDataSource,
	private val dataSourceLocal: IFuelHistoryLocalDataSource,
	private val mappers: FuelHistoryMappersFacade
) : BaseRepository(), IFuelHistoryRepository {
	
	companion object {
		private const val startItemsCount = 20
		private const val startHistoryOffset = 20
	}
	
	
	//offset cursor position
	private var historyOffset = 0
	
	
	
	override suspend fun loadFuelHistory(): SimpleResult<List<FuelHistoryRecord>> =
		dataSourceLocal.getFuelHistory(startItemsCount, startHistoryOffset).fold(
			success = { dto ->
				ResultState.Success(mappers.mapDbHistoryToDm(dto)).also {
					//reset offset
					historyOffset = 0
					//update offset
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	override suspend fun loadMoreFuelHistory(entries: Int): SimpleResult<List<FuelHistoryRecord>> =
		dataSourceLocal.getFuelHistory(entries, historyOffset).fold(
			success = { dto ->
				ResultState.Success(mappers.mapDbHistoryToDm(dto)).also {
					//update offset
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	override suspend fun addFuelHistoryEntry(fuelHistoryRecord: FuelHistoryRecord) =
		dataSourceLocal.insertFuelHistoryEntry(mappers.mapDmHistoryToDb(fuelHistoryRecord))
	
	override suspend fun removeFuelHistoryEntry(fuelHistoryRecord: FuelHistoryRecord) =
		dataSourceLocal.deleteFuelHistoryEntry(mappers.mapDmHistoryToDb(fuelHistoryRecord))
}