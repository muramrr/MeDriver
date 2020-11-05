/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.11.2020 16:27
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
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.user.UserDataInfo
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
	
	private companion object {
		private const val startItemsCount = 20
		private const val startHistoryOffset = 0
	}
	//offset cursor position
	private var historyOffset = 0
	
	
	
	
	/**
	 * Add [history] to local cache
	 * Also check if given user is not null and premium
	 * If that is true -> write also to backend
	 */
	override suspend fun addFuelHistoryRecord(user: UserDataInfo?, history: FuelHistory):
			Flow<SimpleResult<Unit>> = flow {
		
		localDataSource.insertFuelHistoryEntry(mappers.domainToEntity(history)).fold(
			success = { result ->
				//check if user is premium && is sync enabled to write to backend
				if (user != null && user.isSubscriptionValid() && user.isSyncEnabled)
					remoteDataSource.addFuelHistory(
						user.email,
						history.vehicleVinCode,
						mappers.domainToDto(history)
					).collect { emit(it) }
				
				//otherwise result is success because writing to database was successful
				else emit(ResultState.success(result))
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
		
	}
	
	
	override suspend fun loadFuelHistory(
		vin: String,
		size: Int?,
	): SimpleResult<List<FuelHistory>> =
		if (size == null || size < 0) loadFirstFuelHistory(vin)
		else loadMoreFuelHistory(vin, size)
	
	override suspend fun loadFirstFuelHistoryEntry(vin: String): SimpleResult<FuelHistory?> =
		localDataSource.getFirstFuelHistoryEntry(vin).fold(
			success = {
				if (it != null) ResultState.success(mappers.entityToDomain(it))
				else ResultState.success(null)
			},
			failure = { ResultState.Failure(it) }
		)
	
	
	private suspend fun loadFirstFuelHistory(vin: String): SimpleResult<List<FuelHistory>> =
		localDataSource.getFuelHistory(vin, startItemsCount, startHistoryOffset).fold(
			success = { dto ->
				//reset offset
				historyOffset = 0
				
				ResultState.Success(mappers.listEntitiesToDomain(dto)).also {
					//update offset after first items was loaded
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	private suspend fun loadMoreFuelHistory(vin: String, size: Int): SimpleResult<List<FuelHistory>> =
		localDataSource.getFuelHistory(vin, size, historyOffset).fold(
			success = { dto ->
				ResultState.Success(mappers.listEntitiesToDomain(dto)).also {
					//update offset
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	override suspend fun removeFuelHistoryRecord(history: FuelHistory): SimpleResult<Unit> =
		localDataSource.deleteFuelHistoryEntry(mappers.domainToEntity(history))
}