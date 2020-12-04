/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
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

/**
 * Boundary class for downloading vehicle, maintenance and fuel history entries from server
 */

class DataDownloader(
	private val vehicles: IVehicleDownloader,
	private val maintenance: IMaintenanceDownloader,
	private val fuelHistory: IFuelHistoryDownloader,
	private val journal: IJournalDownloader
) {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	suspend fun deleteAll() {
		vehicles.clear()
		maintenance.clear()
		fuelHistory.clear()
		MainActivity.currentVehicle = null
	}
	
	suspend fun downloadData(email: String) = flow {
		vehicles.download(email).collect { result ->
			result.fold(
				success = { vinList ->
					vinList.asFlow().flatMapMerge { vin ->
						logDebug(TAG, "Downloading all info affiliated with vehicle $vin")
						downloadCombination(email, vin)
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
		fuelHistory.download(email, vin)
			.combine(maintenance.download(email, vin)) { fuelResult, maintenanceResult ->
			logDebug(TAG, "Running combination of downloading both fuel and maintenance history...")
			combineResultStates(fuelResult, maintenanceResult)
		}
	
	suspend fun retrievePendingDownloads(email: String) = flow {
		journal.getOperations(email, MedriverApp.lastOperationSyncedId).collect { result ->
			result.fold(
				success = { serverJournal ->
					if (serverJournal.isNotEmpty()) {
						serverJournal.forEach { operation ->
							when (operation.type) {
								MAINTENANCE -> {
									maintenance.downloadSingle(
										email,
										operation.vin,
										operation.documentId
									).collect { emit(it) }
								}
								FUEL_HISTORY -> fuelHistory.downloadSingle(
									email,
									operation.vin,
									operation.documentId
								).collect { emit(it) }
								//todo: download only last operation related to vehicle
								VEHICLE -> vehicles.downloadSingle(
									email, operation.vin
								).collect { emit(it) }
							}
							
						}
					}
				},
				failure = {
					emit(ResultState.failure(it))
				}
			)
			
		}
	}
	
}