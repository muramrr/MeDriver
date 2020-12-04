/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fetching

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.cache.addToBackend
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.server.IVehicleServerDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
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
	private val vehicleServerDS: IVehicleServerDataSource,
	private val mappers: VehicleMappersFacade
): IFetchingRepository, BaseRepository() {
	
	//called only on app startup
	override suspend fun getSavedVehicle(vin: String): Vehicle? = 
		vehicleLocalDS.getVehicle(vin).fold(
			success = { entity -> mappers.entityToDomain(entity) },
			failure = { throwable ->
				logError(TAG, "${throwable.message}")
				null
			}
		)
	
	
	override suspend fun updateVehicle(
		user: UserDataInfo?, vehicle: Vehicle
	): Flow<SimpleResult<Unit>> = flow {
		val entity = mappers.domainToEntity(vehicle)
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
						vehicleServerDS.addVehicle(
							user!!.email,
							mappers.domainToDto(vehicle)
						).collect { emit(it) }
					}
				)
			},
			failure = { throwable -> emit(ResultState.failure(throwable)) }
		)
	}
	
	
	
}