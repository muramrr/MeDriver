/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.maintenance

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.cache.addToBackend
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.server.IMaintenanceServerDataSource
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.user.UserDataInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IMaintenanceRepository] implementation
 */

class MaintenanceRepositoryImpl(
	private val localDataSource: IMaintenanceLocalDataSource,
	private val serverDataSource: IMaintenanceServerDataSource,
	private val mappers: MaintenanceMappersFacade
): IMaintenanceRepository, BaseRepository() {
	
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
	
	
	
	
	override suspend fun addMaintenanceItems(
		user: UserDataInfo?,
		items: List<VehicleSparePart>
	): Flow<SimpleResult<Unit>> = flow {
		val entities = mappers.listDomainsToEntities(items)
		localDataSource.insertReplacedSpareParts(entities).fold(
			success = {
				addToBackend(
					user,
					MedriverApp.isInternetWorking(),
					cacheOperation = { cachingReason ->
						emit(
							localDataSource.cachePendingWriteToBackend(
								entities.map {
									CachedOperation(
										MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE,
										it.dateAdded.toString(),
										cachingReason.name
									)
								}
							)
						)
					},
					serverOperation = {
						serverDataSource.addMaintenanceHistoryItems(
							user!!.email, items.first().vehicleVinCode, mappers.listDomainsToDto(items)
						).collect { emit(it) }
					}
				)
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
	}
	
	override suspend fun findLastReplaced(
		vin: String, systemNode: String, customComponent: String
	): SimpleResult<VehicleSparePart> =
		localDataSource.findLastReplaced(vin, systemNode, customComponent).fold(
			success = { ResultState.success(mappers.entityToDomain(it)) },
			failure = { ResultState.failure(it) }
		)
	
	override suspend fun getInitMaintenanceHistory(vin: String): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getMaintenanceHistory(vin, ITEMS_COUNT_PER_LOAD, NO_OFFSET).fold(
			success = { entities ->
				//reset offset
				nextHistoryOffset = 0
				
				ResultState.success(mappers.listEntitiesToDomains(entities)).also {
					//update offset after first items was loaded
					nextHistoryOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	override suspend fun getMoreMaintenanceHistory(vin: String): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getMaintenanceHistory(vin, ITEMS_COUNT_PER_LOAD, nextHistoryOffset).fold(
			success = { entities ->
				ResultState.success(mappers.listEntitiesToDomains(entities)).also {
					//update offset
					nextHistoryOffset += it.data.size
					//logWtf(TAG, "$nextHistoryOffset")
				}
			},
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	override suspend fun getPreviousMaintenanceHistory(vin: String): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getMaintenanceHistory(vin, ITEMS_COUNT_PER_LOAD, previousHistoryOffset).fold(
			success = { entities ->
				ResultState.success(mappers.listEntitiesToDomains(entities)).also {
					//update offset
					nextHistoryOffset -= it.data.size
				}
			},
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	override suspend fun getSystemNodeHistory(
		vin: String, systemNode: String
	): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getSystemNodeHistory(vin, systemNode).fold(
			success = { ResultState.success(mappers.listEntitiesToDomains(it)) },
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	override suspend fun getHistoryByTypedQuery(
		vin: String, typedQuery: String
	): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getByTypedQuery(vin, typedQuery).fold(
			success = { ResultState.success(mappers.listEntitiesToDomains(it)) },
			failure = { ResultState.failure(it) }
		)
	
	
}