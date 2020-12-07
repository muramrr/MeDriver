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

package com.mmdev.me.driver.data.sync.download

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.data.core.firebase.ServerOperation
import com.mmdev.me.driver.data.core.firebase.ServerOperationType.*
import com.mmdev.me.driver.data.sync.download.fuel.IFuelHistoryDownloader
import com.mmdev.me.driver.data.sync.download.journal.IJournalDownloader
import com.mmdev.me.driver.data.sync.download.maintenance.IMaintenanceDownloader
import com.mmdev.me.driver.data.sync.download.vehicle.IVehicleDownloader
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.combineResultStates
import com.mmdev.me.driver.presentation.ui.MainActivity
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * Boundary class for downloading vehicle, maintenance and fuel history entries from server
 */

class DataDownloader(
	private val vehicles: IVehicleDownloader,
	private val maintenance: IMaintenanceDownloader,
	private val fuelHistory: IFuelHistoryDownloader,
	private val journal: IJournalDownloader
): IDataDownloader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	suspend fun deleteAll() {
		vehicles.clear()
		maintenance.clear()
		fuelHistory.clear()
		MainActivity.currentVehicle = null
	}
	
	override suspend fun importData(email: String) = flow {
		vehicles.download(email).collect { result ->
			result.fold(
				success = { vinList ->
					vinList.asFlow().flatMapMerge { vin ->
						logDebug(TAG, "Downloading all info affiliated with vehicle $vin")
						importCombination(email, vin)
					}.collect { emit(it) }
				},
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	}
	
	private suspend fun importCombination(email: String, vin: String) =
		fuelHistory.download(email, vin)
			.combine(maintenance.download(email, vin)) { fuelResult, maintenanceResult ->
			logDebug(TAG, "Running combination of downloading both fuel and maintenance history...")
			combineResultStates(fuelResult, maintenanceResult)
		}
	
	override suspend fun fetchNewFromServer(email: String) = flow {
		journal.getOperations(email, MedriverApp.lastOperationSyncedId).collect { result ->
			logDebug(TAG, "Getting server journal...")
			result.fold(
				success = { serverJournal ->
					if (serverJournal.isNotEmpty()) {
						logInfo(TAG, "Retrieved server operations journal is not empty, pending downloads exists, executing...")
						downloadNewFromServer(serverJournal, email).collect { emit(it) }
					}
					else {
						logDebug(TAG, "Server journal is empty...")
						emit(ResultState.success(Unit))
					}
				},
				failure = {
					emit(ResultState.failure(it))
				}
			)
			
		}
	}
	
	override suspend fun downloadNewFromServer(operations: List<ServerOperation>, email: String) = flow {
		// map<ServerOperationType, List<ServerOperation>
		val groupedOperations = operations.groupBy { it.type }
		
		logWtf(TAG, "Grouped: $groupedOperations")
		
		/**
		 *  list of last vehicle updates, linked to each vehicle separately
		 *  eg: server journal contains a list of vehicle update operations, but we want to get only
		 *  last one update to being retrieved and inserted, here comes a problem that a journal may
		 *  contains a list of operations for different vehicles, and for each vehicle need to
		 *  retrieve only the last update operation
		 *
		 *  result will contain map "VIN" to {list of one operation, only last one}
		 *  we need only values from that result
		 */
		val vehiclesOperations = groupedOperations[VEHICLE]
			?.groupBy { vehicleOperation -> vehicleOperation.vin } //group by vin
			?.mapValues { entry -> entry.value.maxByOrNull { it.dateAdded }!! } //filter each group
			?.values ?: emptyList() //if no vehicle operations at all - return emptyList
		
		logWtf(TAG, "Vehicles Grouped and filtered: $vehiclesOperations")
		
		val filteredOperations = listOf(
			vehiclesOperations,
			groupedOperations.getOrDefault(MAINTENANCE, emptyList()),
			groupedOperations.getOrDefault(FUEL_HISTORY, emptyList())
		).flatten()
			
		// not null find the lastest updating date value of vehicle
		
		filteredOperations.asFlow().flatMapMerge(filteredOperations.size) { operation ->
			when (operation.type) {
				MAINTENANCE ->  maintenance.downloadSingle(
					email,
					operation.vin,
					operation.documentId
				)
				FUEL_HISTORY -> fuelHistory.downloadSingle(
					email,
					operation.vin,
					operation.documentId
				)
				VEHICLE -> vehicles.downloadSingle(
					email,
					operation.vin
				)
				else -> {
					flowOf(
						ResultState.failure(
							Exception("Unsupported server operation type")
						)
					)
				}
			}
			
		}.collect { emit(it) }
	}
	
}