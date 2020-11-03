/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.11.2020 18:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.remote.IFuelHistoryRemoteDataSource
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.remote.IMaintenanceRemoteDataSource
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.remote.IVehicleRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.combineWith
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 *
 */

class DataManipulator(
	private val vehicleLocalDataSource: IVehicleLocalDataSource,
	private val vehicleRemoteDataSource: IVehicleRemoteDataSource,
	private val maintenanceLocalDataSource: IMaintenanceLocalDataSource,
	private val maintenanceRemoteDataSource: IMaintenanceRemoteDataSource,
	private val historyLocalDataSource: IFuelHistoryLocalDataSource,
	private val historyRemoteDataSource: IFuelHistoryRemoteDataSource,
	private val vehicleMappersFacade: VehicleMappersFacade,
	private val maintenanceMappersFacade: MaintenanceMappersFacade,
	private val historyMappersFacade: FuelHistoryMappersFacade
) {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	suspend fun deleteAll() {
		vehicleLocalDataSource.clearAll()
		maintenanceLocalDataSource.clearAll()
		historyLocalDataSource.clearAll()
	}
	
	suspend fun downloadData(email: String) = flow {
		vehicleRemoteDataSource.getAllVehicles(email).collect { result ->
			result.fold(
				success = { vehicles ->
					
					vehicles.asFlow().flatMapMerge { vehicleDto ->
						logDebug(TAG, "Inserting vehicle: ${vehicleDto.brand} ${vehicleDto.model}")
						
						vehicleLocalDataSource.insertVehicle(
							vehicleMappersFacade.apiDtoToEntity(vehicleDto)
						).fold(
							success = {
								logDebug(TAG, "Downloading all info affiliated with vehicle " +
								          "${vehicleDto.brand} ${vehicleDto.model}")
								downloadCombination(email, vehicleDto.vin)
							},
							failure = {
								logError(TAG, "${it.message}")
								flowOf(ResultState.failure(it))
							}
						)
					}.collect { emit(it) }
				},
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	}
	
	private suspend fun downloadCombination(email: String, vin: String) =
		downloadFuelHistory(email, vin).combine(
			downloadMaintenanceHistory(email, vin)
		) { fuel, maintenance ->
			logDebug(TAG, "Running combination of writing both fuel and maintenance history...")
			fuel.combineWith(maintenance)
		}
	
	private suspend fun downloadFuelHistory(
		email: String, vin: String
	) = flow {
		historyRemoteDataSource.getFuelHistory(email, vin).collect { result ->
			result.fold(
				success = {
					emit(
						historyLocalDataSource.importFuelHistory(
							historyMappersFacade.listDtoToEntities(it)
						)
					)
				},
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	}
	
	private suspend fun downloadMaintenanceHistory(
		email: String, vin: String
	) = flow {
		maintenanceRemoteDataSource.getMaintenanceHistory(email, vin).collect { result ->
			result.fold(
				success = {
					emit(
						maintenanceLocalDataSource.insertReplacedSpareParts(
							maintenanceMappersFacade.listDtoToEntities(it)
						)
					)
				},
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	}
	
}