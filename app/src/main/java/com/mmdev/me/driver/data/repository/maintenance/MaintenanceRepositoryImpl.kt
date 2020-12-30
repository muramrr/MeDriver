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

package com.mmdev.me.driver.data.repository.maintenance

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.server.IMaintenanceServerDataSource
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
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
): BaseRepository(), IMaintenanceRepository {
	
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
	
	
	
	
	override fun addMaintenanceItems(
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
		vin: String, systemNode: VehicleSystemNodeType, component: SparePart
	): SimpleResult<VehicleSparePart> =
		localDataSource.findLastReplaced(vin, systemNode.toString(), component.getSparePartName()).fold(
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
		vin: String, systemNode: VehicleSystemNodeType
	): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getSystemNodeHistory(vin, systemNode.toString()).fold(
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
	
	override fun removeMaintenanceEntry(user: UserDataInfo?, maintenance: VehicleSparePart):
			Flow<SimpleResult<Unit>> = flow {
		localDataSource.deleteMaintenanceHistoryEntry(maintenance.dateAdded).fold(
			success = {
				if (user?.isPro() == true && MedriverApp.isInternetWorking()) {
					serverDataSource.deleteMaintenanceEntry(
						user.email, mappers.domainToDto(maintenance)
					).collect { emit(it) }
				}
				else emit(ResultState.success(Unit))
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
	}
}