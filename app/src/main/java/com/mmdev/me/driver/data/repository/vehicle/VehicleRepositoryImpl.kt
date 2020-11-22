/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 14:54
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vehicle

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.cache.addToBackend
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.remote.IVehicleRemoteDataSource
import com.mmdev.me.driver.data.datasource.vin.local.IVinLocalDataSource
import com.mmdev.me.driver.data.datasource.vin.remote.IVinRemoteDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IVehicleRepository] implementation
 */

class VehicleRepositoryImpl(
	private val localDataSource: IVehicleLocalDataSource,
	private val remoteDataSource: IVehicleRemoteDataSource,
	private val localVinDecoder: IVinLocalDataSource, //todo
	private val remoteVinDecoder: IVinRemoteDataSource,
	private val mappers: VehicleMappersFacade
): IVehicleRepository, BaseRepository() {
	
	
	/**
	 * Add new [Vehicle] to database and remote
	 * Invoked when user adds a new vehicle inside [VehicleFragment] by typing base information
	 *
	 * @return [ResultState.Failure] when failure occurred
	 * basically when [ResultState] emits from [remoteDataSource] no matter what it would be
	 * local data will be written without being obstructed
	 */
	override suspend fun addVehicle(
		user: UserDataInfo?,
		vehicle: Vehicle
	): Flow<SimpleResult<Unit>> = flow {
		val entity = mappers.domainToEntity(vehicle)
		localDataSource.insertVehicle(entity).fold(
			success = {
				addToBackend(
					user,
					MedriverApp.isInternetWorking(),
					cacheOperation = { reason ->
						emit(
							localDataSource.cachePendingWriteToBackend(
								CachedOperation(
									MeDriverRoomDatabase.VEHICLES_TABLE,
									entity.vin,
									reason.name
								)
							)
						)
					},
					serverOperation = {
						remoteDataSource.addVehicle(
							user!!.email, mappers.domainToDto(vehicle)
						).collect { emit(it) }
					}
				)
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
	}
	
	override suspend fun getExpensesInfo(vin: String): SimpleResult<Expenses> =
		localDataSource.getExpenses(vin)
	
	override suspend fun getPendingReplacements(vehicle: Vehicle): SimpleResult<Map<SparePart, PendingReplacement?>> =
		localDataSource.gePlannedReplacements(vehicle.vin).fold(
			success = { ResultState.success(mappers.sparePartToReplacementCalculated(it, vehicle)) },
			failure = { ResultState.failure(it) }
		)
	
	
	/**
	 * Get list of [Vehicle] from cache.
	 * @return [ResultState.Success] when list contains data and convert it
	 * @return [ResultState.Failure] when list is empty or failure occurred
	 */
	override suspend fun getAllSavedVehicles(): SimpleResult<List<Vehicle>> =
		localDataSource.getAllVehicles().fold(
			success = { data ->
				if (data.isNotEmpty()) ResultState.success(mappers.listEntitiesToDomain(data))
				else ResultState.failure(Exception("Empty cache"))
			},
			failure = { ResultState.failure(it) }
		)
	
	
	/**
	 * Used to retrieve vehicle base info by typed in VIN code
	 * Invoked from [com.mmdev.me.driver.presentation.ui.vehicle.VehicleAddBottomSheet]
	 *
	 * @return [ResultState.Success] when response contains data some
	 * @return [ResultState.Failure] when response is empty or failure occurred
	 */
	override suspend fun getVehicleInfoByVin(vin: String): SimpleResult<Vehicle> =
		remoteVinDecoder.getVehicleByVin(vin).fold(
			success = { dto ->
				if (dto.results.isNotEmpty())
					ResultState.Success(mappers.vinApiDtoToDomain(dto.results.first()))
				else ResultState.Failure(Exception("No data or vehicle is not found."))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
}