/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.10.2020 18:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.maintenance

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.remote.IMaintenanceRemoteDataSource
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.user.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IMaintenanceRepository] implementation
 */

class MaintenanceRepositoryImpl(
	private val localDataSource: IMaintenanceLocalDataSource,
	private val remoteDataSource: IMaintenanceRemoteDataSource,
	private val mappers: MaintenanceMappersFacade
): IMaintenanceRepository, BaseRepository() {
	
	private companion object {
		private const val startItemsCount = 20
		private const val startHistoryOffset = 0
	}
	
	//offset cursor position
	private var historyOffset = 0
	
	
	
	
	override suspend fun addMaintenanceItems(
		user: UserData?,
		items: List<VehicleSparePart>
	): Flow<SimpleResult<Unit>> = flow {
		localDataSource.insertReplacedSpareParts(mappers.listDomainsToEntities(items)).fold(
			success = { result ->
				//check if user is premium && is sync enabled to write to backend
				if (user != null && user.isPremium && user.isSyncEnabled)
					remoteDataSource.addMaintenanceHistoryItems(
						user.email,
						items.first().vehicleVinCode,
						mappers.listDomainsToDto(items)
					).collect { emit(it) }
				
				//otherwise result is success because writing to database was successful
				else emit(ResultState.success(result))
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
	
	override suspend fun getMaintenanceHistory(
		vin: String, size: Int?
	): SimpleResult<List<VehicleSparePart>> =
		if (size == null || size < 0) loadFirstHistory(vin)
		else loadMoreHistory(vin, size)
	
	
	private suspend fun loadFirstHistory(vin: String): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getMaintenanceHistory(vin, startItemsCount, startHistoryOffset).fold(
			success = { entities ->
				//reset offset
				historyOffset = 0
				
				ResultState.Success(mappers.listEntitiesToDomains(entities)).also {
					//update offset after first items was loaded
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	private suspend fun loadMoreHistory(vin: String, size: Int): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getMaintenanceHistory(vin, size, historyOffset).fold(
			success = { entities ->
				ResultState.Success(mappers.listEntitiesToDomains(entities)).also {
					//update offset
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	
	
	override suspend fun getSystemNodeHistory(
		vin: String, systemNode: String
	): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getSystemNodeHistory(vin, systemNode).fold(
			success = { ResultState.success(mappers.listEntitiesToDomains(it)) },
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	override suspend fun getHistoryByTypedQuery(
		vin: String, typedQuery: String
	): SimpleResult<List<VehicleSparePart>> =
		localDataSource.getByTypedQuery(vin, typedQuery).fold(
			success = { ResultState.success(mappers.listEntitiesToDomains(it)) },
			failure = { ResultState.failure(it) }
		)
	
	
}