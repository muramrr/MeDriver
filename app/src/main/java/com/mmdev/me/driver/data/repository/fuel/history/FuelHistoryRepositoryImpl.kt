/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.cache.addToBackend
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.server.IFuelHistoryServerDataSource
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
	private val serverDataSource: IFuelHistoryServerDataSource,
	private val mappers: FuelHistoryMappersFacade
) : BaseRepository(), IFuelHistoryRepository {
	
	private companion object {
		private const val ITEMS_COUNT_PER_LOAD = 20
		private const val ITEMS_COUNT_IN_POOL = ITEMS_COUNT_PER_LOAD * 2
		private const val NO_OFFSET = 0
	}
	
	//offset cursor position, also represents how many items we've loaded
	private var nextHistoryOffset = 0
		private set(value) {
			field = value
			if (value > ITEMS_COUNT_IN_POOL){
				previousHistoryOffset = value - ITEMS_COUNT_IN_POOL - ITEMS_COUNT_PER_LOAD
			}
		}
	private var previousHistoryOffset = 0
		private set(value) {
			field = if (value < 0) 0
			else value
		}
	
	
	
	
	/**
	 * Add [history] to local cache
	 * Also check if given user is not null and premium
	 * If that is true -> write also to backend
	 */
	override suspend fun addFuelHistoryRecord(
		user: UserDataInfo?, history: FuelHistory
	): Flow<SimpleResult<Unit>> = flow {
		val entity = mappers.domainToEntity(history)
		localDataSource.insertFuelHistoryEntry(entity).fold(
			success = {
				//check if user is premium && is sync enabled && network is accessible
				addToBackend(
					user,
					MedriverApp.isInternetWorking(),
					cacheOperation = { reason ->
						emit(
							localDataSource.cachePendingWriteToBackend(
								CachedOperation(
									MeDriverRoomDatabase.FUEL_HISTORY_TABLE,
									entity.dateAdded.toString(),
									reason.name
								)
							)
						)
					},
					serverOperation = {
						serverDataSource.addFuelHistory(
							user!!.email, history.vehicleVinCode, mappers.domainToDto(history)
						).collect { emit(it) }
					}
				)
				
			}, failure = { throwable -> emit(ResultState.failure(throwable)) })
		
	}
	
	override suspend fun importFuelHistory(
		user: UserDataInfo?, history: List<FuelHistory>
	): Flow<SimpleResult<Unit>> = flow {
		localDataSource.importFuelHistory(mappers.listDomainToEntities(history)).fold(
			success = { emit(ResultState.success(Unit)) },
			failure = { emit(ResultState.failure(it)) }
		)
	}
	
	override suspend fun loadFirstFuelHistoryEntry(vin: String): SimpleResult<FuelHistory?> =
		localDataSource.getFirstFuelHistoryEntry(vin).fold(
			success = {
				if (it != null) ResultState.success(mappers.entityToDomain(it))
				else ResultState.success(null)
			},
			failure = { ResultState.failure(it) }
		)
	
	
	override suspend fun getInitFuelHistory(vin: String): SimpleResult<List<FuelHistory>> =
		localDataSource.getFuelHistory(vin, ITEMS_COUNT_PER_LOAD, NO_OFFSET).fold(
			success = { dto ->
				//reset offset
				nextHistoryOffset = 0
				
				ResultState.Success(mappers.listEntitiesToDomain(dto)).also {
					//update offset after first items was loaded
					nextHistoryOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	override suspend fun getMoreFuelHistory(vin: String): SimpleResult<List<FuelHistory>> =
		localDataSource.getFuelHistory(vin, ITEMS_COUNT_PER_LOAD, nextHistoryOffset).fold(
			success = { dto ->
				ResultState.success(mappers.listEntitiesToDomain(dto)).also {
					//update offset
					nextHistoryOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	override suspend fun getPreviousFuelHistory(vin: String): SimpleResult<List<FuelHistory>> =
		localDataSource.getFuelHistory(vin, ITEMS_COUNT_PER_LOAD, previousHistoryOffset).fold(
			success = { dto ->
				ResultState.success(mappers.listEntitiesToDomain(dto)).also {
					//update offset
					nextHistoryOffset -= it.data.size
				}
			},
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	override suspend fun removeFuelHistoryRecord(history: FuelHistory): SimpleResult<Unit> =
		localDataSource.deleteFuelHistoryEntry(mappers.domainToEntity(history))
}