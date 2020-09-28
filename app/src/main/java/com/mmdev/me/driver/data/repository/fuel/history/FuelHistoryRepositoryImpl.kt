/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.09.2020 18:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.remote.IFuelHistoryRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.user.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IFuelHistoryRepository] implementation
 */

class FuelHistoryRepositoryImpl (
	private val localDataSource: IFuelHistoryLocalDataSource,
	private val remoteDataSource: IFuelHistoryRemoteDataSource,
	private val mappers: FuelHistoryMappersFacade
) : BaseRepository(), IFuelHistoryRepository {
	
	companion object {
		private const val startItemsCount = 20
		private const val startHistoryOffset = 0
	}
	
	
	//offset cursor position
	private var historyOffset = 0
	
	
	override suspend fun loadFuelHistory(
		vin: String,
		size: Int?,
	): SimpleResult<List<FuelHistoryRecord>> =
		if (size == null || size < 0) loadFirstFuelHistory(vin)
		else loadMoreFuelHistory(vin, size)
	
	
	private suspend fun loadFirstFuelHistory(vin: String): SimpleResult<List<FuelHistoryRecord>> =
		localDataSource.getFuelHistory(vin, startItemsCount, startHistoryOffset).fold(
			success = { dto ->
				//reset offset
				historyOffset = 0
				
				ResultState.Success(mappers.listDbEntitiesToDomains(dto)).also {
					//update offset after first items was loaded
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	private suspend fun loadMoreFuelHistory(vin: String, size: Int): SimpleResult<List<FuelHistoryRecord>> =
		localDataSource.getFuelHistory(vin, size, historyOffset).fold(
			success = { dto ->
				ResultState.Success(mappers.listDbEntitiesToDomains(dto)).also {
					//update offset
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	
	/**
	 * Add [historyRecord] to local cache
	 * Also check if given user is not null and premium
	 * If that is true -> write also to backend
	 */
	override suspend fun addFuelHistoryRecord(user: UserModel?, historyRecord: FuelHistoryRecord):
			Flow<SimpleResult<Unit>> = flow {
		
		localDataSource.insertFuelHistoryEntry(mappers.domainToDbEntity(historyRecord)).fold(
			success = { result ->
				//check if user is premium && is sync enabled to write to backend
				if (user != null && user.isPremium && user.isSyncEnabled)
					remoteDataSource.addFuelHistory(
						user.email,
						historyRecord.vehicleVinCode,
						mappers.domainToApiDto(historyRecord)
					).collect { emit(it) }
					
				//otherwise result is success because writing to database was successful
				else emit(ResultState.success(result))
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
		
	}
	
	
	
	
	
	override suspend fun removeFuelHistoryRecord(historyRecord: FuelHistoryRecord): SimpleResult<Unit> =
		localDataSource.deleteFuelHistoryEntry(mappers.domainToDbEntity(historyRecord))
}