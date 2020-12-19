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

package com.mmdev.me.driver.data.repository.vehicle

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.server.IVehicleServerDataSource
import com.mmdev.me.driver.data.datasource.vin.api.IVinApiDataSource
import com.mmdev.me.driver.data.datasource.vin.local.IVinLocalDataSource
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
	private val serverDataSource: IVehicleServerDataSource,
	private val localVinDecoder: IVinLocalDataSource, //todo
	private val remoteVinDecoder: IVinApiDataSource,
	private val mappers: VehicleMappersFacade
): BaseRepository(), IVehicleRepository {
	
	
	override fun addVehicle(
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
						serverDataSource.addVehicle(
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
	
	
	override suspend fun getAllSavedVehicles(): SimpleResult<List<Vehicle>> =
		localDataSource.getAllVehicles().fold(
			success = { data ->
				if (data.isNotEmpty()) ResultState.success(mappers.listEntitiesToDomain(data))
				else ResultState.failure(Exception("Empty cache"))
			},
			failure = { ResultState.failure(it) }
		)
	
	override suspend fun getSavedVehicle(vin: String): Vehicle? =
		localDataSource.getVehicle(vin).fold(
			success = { entity ->
				if (entity != null) {
					mappers.entityToDomain(entity)
				}
				else null
			},
			failure = { throwable ->
				logError(TAG, "${throwable.message}")
				null
			}
		)
	
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