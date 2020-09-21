/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 20:26
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
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IVehicleRepository] implementation
 */

class VehicleRepositoryImpl(
	private val localDataSource: IVehicleLocalDataSource,
	private val remoteDataSource: IVehicleRemoteDataSource,
	private val mappers: VehicleMappersFacade
) : IVehicleRepository, BaseRepository() {
	
	/**
	 * Get list of [Vehicle].
	 * First of all, try to get it from local database.
	 * If there is data, request to backend won't be executed.
	 * @see getAllVehiclesFromCache
	 * If there is empty list - check is user premium and request data from backend.
	 * @see getAllVehiclesFromBackend
	 * Otherwise return emptyList()
	 *
	 * @return [ResultState.Success] if list contains data or not
	 * @return [ResultState.Failure] if failure occurs
	 */
	override suspend fun getAllVehicles(user: UserModel?): Flow<SimpleResult<List<Vehicle>>> = flow {
		getAllVehiclesFromCache().fold(
			success = { data -> emit(ResultState.success(data)) },
			failure = { throwable ->
				logError(TAG, throwable.message ?: "Boundary local cache error")
				if (user != null && user.isPremium) getAllVehiclesFromBackend(user).collect { emit(it) }
				else emit(ResultState.success(emptyList<Vehicle>()))
			}
		)
	}
	
	/**
	 * Get list of [Vehicle] from cache.
	 * @return [ResultState.Success] if list contains data and convert it
	 * @return [ResultState.Failure] if list is empty
	 */
	private suspend fun getAllVehiclesFromCache(): SimpleResult<List<Vehicle>> =
		localDataSource.getAllVehicles().fold(
			success = { data ->
				if (data.isNotEmpty()) ResultState.success(mappers.listDbEntityToDomain(data))
				else ResultState.failure(Exception("Empty cache"))
			},
			failure = { throwable -> ResultState.failure(throwable) }
		)
	
	
	/**
	 * Get list of [Vehicle] from backend if user is logged in.
	 * Uses when local database is empty.
	 * This method invokes only when we know for sure that this user is premium
	 * @see getAllVehicles
	 * Collect result from request, save it to local cache and return
	 *
	 * @return [ResultState.Success] if list contains data, convert it, save to local database
	 * @return [ResultState.Failure] if error occurs
	 */
	private fun getAllVehiclesFromBackend(user: UserModel): Flow<SimpleResult<List<Vehicle>>> = flow {
		try {
			remoteDataSource.getAllVehicles(user.email).collect { result ->
				result.fold(success = { data ->
					data.forEach {
						localDataSource.insertVehicle(mappers.apiDtoToDbEntity(it)).fold(
							success = {},
							failure = { throwable -> emit(ResultState.failure(throwable)) }
						)
					}
					emit(ResultState.success(mappers.listApiDtoToDomain(data)))
				}, failure = { throwable -> emit(ResultState.failure(throwable)) })
			}
		}
		catch (e: Exception) {
			logError(TAG, "${e.message}")
			emit(ResultState.failure(e))
		}
		
	}
	
	
	
	
	
	override suspend fun getVehicle(vin: String): Vehicle? = localDataSource.getVehicle(vin).fold(
		success = { dto -> mappers.dbEntityToDomain(dto) },
		failure = { throwable -> null }
	)
	
	
	
	override suspend fun addVehicle(
		user: UserModel?, vehicle: Vehicle
	): Flow<SimpleResult<Unit>> = flow {
		localDataSource.insertVehicle(mappers.domainToDbEntity(vehicle)).fold(
			success = { result ->
				//check if user is premium to backup to backend
				if (user != null && user.isPremium)
					remoteDataSource.addVehicle(user.email, mappers.domainToApiDto(vehicle)).collect {
						emit(it)
					}
				//otherwise result is success because writing to database was successful
				else emit(ResultState.success(result))
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
	}
	
	
	
	
	
}