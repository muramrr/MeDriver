/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.11.2020 19:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fetching

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.cache.addToBackend
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
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
			success = { entity ->
				logWtf(javaClass, "${vehicleLocalDS.getExpenses(vin)}")
				mappers.vehicleDbToDomain(entity)
			},
			failure = { throwable -> null }
		)
	
	
	override suspend fun updateVehicle(
		user: UserDataInfo?, vehicle: Vehicle
	): Flow<SimpleResult<Unit>> = flow {
		val entity = mappers.vehicleDomainToDb(vehicle)
		vehicleLocalDS.insertVehicle(entity).fold(
			success = {
				//check if user is premium && is sync enabled && network is accessible
				addToBackend(
					user,
					MedriverApp.isInternetWorking(),
					cacheOperation = { reason ->
						emit(
							vehicleLocalDS.cachePendingWriteToBackend(
								CachedOperation(
									MeDriverRoomDatabase.VEHICLES_TABLE,
									entity.vin,
									reason.name
								)
							)
						)
					},
					serverOperation = {
						vehicleRemoteDS.addVehicle(
							user!!.email,
							mappers.vehicleDomainToApiDto(vehicle)
						).collect { emit(it) }
					}
				)
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
	}
	
	
	
}