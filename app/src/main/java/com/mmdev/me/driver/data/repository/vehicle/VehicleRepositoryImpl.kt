/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 14:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vehicle

import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.remote.IVehicleRemoteDataSource
import com.mmdev.me.driver.data.datasource.vin.local.IVinLocalDataSource
import com.mmdev.me.driver.data.datasource.vin.remote.IVinRemoteDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserData
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
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
) : IVehicleRepository, BaseRepository() {
	
	
	/**
	 * Add new [Vehicle] to database and remote
	 * Invoked when user adds a new vehicle inside [VehicleFragment] by typing base information
	 *
	 * @return [ResultState.Failure] when failure occurred
	 * basically when [ResultState] emits from [remoteDataSource] no matter what it would be
	 * local data will be written without being obstructed
	 */
	override suspend fun addVehicle(
		user: UserData?, vehicle: Vehicle
	): Flow<SimpleResult<Unit>> = flow {
		localDataSource.insertVehicle(mappers.domainToEntity(vehicle)).fold(
			success = { result ->
				//check if user is premium to backup to backend
				if (user != null && user.isPremium && user.isSyncEnabled)
					remoteDataSource.addVehicle(user.email, mappers.domainToApiDto(vehicle)).collect {
						emit(it)
					}
				//otherwise result is success because writing to database was successful
				else emit(ResultState.success(result))
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
	}
	
	
	
	
	/**
	 * Get list of [Vehicle].
	 * First of all, try to get it from local database.
	 * If there is data, request to backend won't be executed.
	 * @see getAllVehiclesFromCache
	 * If there is empty list - check is user premium and request data from backend.
	 * @see getAllVehiclesFromBackend
	 * Otherwise return emptyList()
	 *
	 * @return [ResultState.Success] when list contains data or not
	 * @return [ResultState.Failure] when failure occurred
	 */
	override suspend fun getAllSavedVehicles(user: UserData?): Flow<SimpleResult<List<Vehicle>>> = flow {
		getAllVehiclesFromCache().fold(
			success = { data -> emit(ResultState.success(data)) },
			failure = { throwable ->
				logError(TAG, throwable.message ?: "Boundary local cache error")
				if (user != null && user.isPremium && user.isSyncEnabled)
					getAllVehiclesFromBackend(user).collect { emit(it) }
				else emit(ResultState.success(emptyList<Vehicle>()))
			}
		)
	}
	
	/**
	 * Get list of [Vehicle] from cache.
	 * @return [ResultState.Success] when list contains data and convert it
	 * @return [ResultState.Failure] when list is empty or failure occurred
	 */
	private suspend fun getAllVehiclesFromCache(): SimpleResult<List<Vehicle>> =
		localDataSource.getAllVehicles().fold(
			success = { data ->
				if (data.isNotEmpty()) ResultState.success(mappers.listEntitiesToDomain(data))
				else ResultState.failure(Exception("Empty cache"))
			},
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	
	/**
	 * Get list of [Vehicle] from backend if user is logged in.
	 * Uses when local database is empty.
	 * This method invokes only when we know for sure that this user is premium
	 * @see getAllSavedVehicles
	 * Collect result from request, save it to local cache and return
	 *
	 * @return [ResultState.Success] when list contains data, convert it, save to local database
	 * @return [ResultState.Failure] when failure occurred
	 */
	private fun getAllVehiclesFromBackend(user: UserData): Flow<SimpleResult<List<Vehicle>>> = flow {
		try {
			remoteDataSource.getAllVehicles(user.email).collect { result ->
				result.fold(
					success = { data ->
						data.forEach { dto ->
							localDataSource.insertVehicle(mappers.apiDtoToEntity(dto)).fold(
								success = {},
								failure = { emit(ResultState.failure(it)) }
							)
						}
						emit(ResultState.success(mappers.listApiDtoToDomain(data)))
					},
					failure = { throwable -> emit(ResultState.failure(throwable)) })
			}
		}
		catch (e: Exception) {
			logError(TAG, "${e.message}")
			emit(ResultState.failure(e))
		}
		
	}
	
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