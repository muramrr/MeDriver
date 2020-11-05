/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.11.2020 16:28
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fetching

import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.remote.IVehicleRemoteDataSource
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fetching.IFetchingRepository
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * [IFetchingRepository] implementation
 */

class FetchingRepositoryImpl(
	private val vehicleLocalDS: IVehicleLocalDataSource,
	private val vehicleRemoteDS: IVehicleRemoteDataSource,
	private val mappers: FetchingMappersFacade
): IFetchingRepository {
	
	//called only on app startup
	override suspend fun getSavedVehicle(vin: String): Vehicle? = 
		vehicleLocalDS.getVehicle(vin).fold(
			success = { entity -> mappers.vehicleDbToDomain(entity) },
			failure = { throwable -> null }
		)
	
	
	override suspend fun updateVehicle(
		user: UserDataInfo?, vehicle: Vehicle
	): Flow<SimpleResult<Unit>> = flow {
		vehicleLocalDS.insertVehicle(mappers.vehicleDomainToDb(vehicle)).fold(
			success = { result ->
				//check if user is premium && is sync enabled to backup to backend
				if (user != null && user.isSubscriptionValid() && user.isSyncEnabled)
					vehicleRemoteDS.addVehicle(user.email, mappers.vehicleDomainToApiDto(vehicle))
						.collect { emit(it) }
				//otherwise result is success because writing to database was successful
				else emit(ResultState.success(result))
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
	}
	
	
	
}