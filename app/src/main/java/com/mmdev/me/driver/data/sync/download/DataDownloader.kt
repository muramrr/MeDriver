/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.12.2020 14:06
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
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
						serverJournal.asFlow().flatMapMerge(serverJournal.size) { operation ->
							when (operation.type) {
								MAINTENANCE -> {
									maintenance.downloadSingle(
										email,
										operation.vin,
										operation.documentId
									)
								}
								FUEL_HISTORY -> fuelHistory.downloadSingle(
									email,
									operation.vin,
									operation.documentId
								)
								//todo: download only last operation related to vehicle
								VEHICLE -> vehicles.downloadSingle(
									email, operation.vin
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
	
}