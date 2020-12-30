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
import com.mmdev.me.driver.data.datasource.fetching.data.ServerDocumentType.*
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperation
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperationType.*
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperationType.UNKNOWN
import com.mmdev.me.driver.data.sync.download.fuel.IFuelHistoryDownloader
import com.mmdev.me.driver.data.sync.download.journal.IJournalDownloader
import com.mmdev.me.driver.data.sync.download.maintenance.IMaintenanceDownloader
import com.mmdev.me.driver.data.sync.download.vehicle.IVehicleDownloader
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.core.combineResultStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform

/**
 * [IDownloader] implementation
 */

class Downloader(
	private val vehicles: IVehicleDownloader,
	private val maintenance: IMaintenanceDownloader,
	private val fuelHistory: IFuelHistoryDownloader,
	private val journal: IJournalDownloader
): IDownloader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun deleteAll() {
		MedriverApp.isDataImported = false // indicates that need a whole data import again
		logDebug(TAG, "Clearing data")
		vehicles.clear()
		maintenance.clear()
		fuelHistory.clear()
	}
	
	override fun importData(email: String): Flow<SimpleResult<Unit>> =
		vehicles.download(email).transform { result ->
			logInfo(TAG, "Running full data import")
			result.fold(
				success = { vinList ->
					if (vinList.isNullOrEmpty()) emit(ResultState.Success(Unit))
					else {
						vinList.asFlow().flatMapMerge { vin ->
							logDebug(TAG, "Downloading all info affiliated with vehicle $vin")
							importCombination(email, vin)
						}.collect {
							// data was imported successfully
							if (it is ResultState.Success) MedriverApp.isDataImported = true
							emit(it)
						}
					}
				},
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	
	
	private fun importCombination(email: String, vin: String): Flow<SimpleResult<Unit>> =
		fuelHistory.download(email, vin)
			.combine(maintenance.download(email, vin)) { fuelResult, maintenanceResult ->
			logDebug(TAG, "Running combination of downloading both fuel and maintenance history...")
			combineResultStates(fuelResult, maintenanceResult)
		}
	
	override fun fetchNewFromServer(email: String): Flow<SimpleResult<Unit>> =
		journal.getOperations(email, MedriverApp.lastOperationSyncedId).transform { result ->
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
	
	
	override fun downloadNewFromServer(
		operations: List<ServerOperation>,
		email: String
	): Flow<SimpleResult<Unit>> = flow {
		if (operations.isNotEmpty()) {
			operations.asFlow().flatMapMerge { operation ->
				when (operation.documentType) {
					MAINTENANCE -> {
						when (operation.operationType) {
							ADDED -> maintenance.downloadSingle(email, operation.vin, operation.documentId)
							DELETED -> flowOf(maintenance.deleteSingle(email, operation.documentId))
							UNKNOWN -> flowOf(ResultState.failure(Exception("Unsupported server operation type")))
						}
					}
					FUEL_HISTORY -> {
						when (operation.operationType) {
							ADDED -> fuelHistory.downloadSingle(email, operation.vin, operation.documentId)
							DELETED -> flowOf(fuelHistory.deleteSingle(email, operation.documentId))
							UNKNOWN -> flowOf(ResultState.failure(Exception("Unsupported server operation type")))
						}
					}
					VEHICLE -> {
						when (operation.operationType) {
							ADDED -> vehicles.downloadSingle(email, operation.vin, operation.dateAdded.toString())
							DELETED -> flowOf(vehicles.deleteSingle(email, operation.vin))
							UNKNOWN -> flowOf(ResultState.failure(Exception("Unsupported server operation type")))
						}
						
					}
					else -> flowOf(ResultState.failure(Exception("Unsupported server document type")))
				}
				
			}.collect { emit(it) }
		}
		else emit(ResultState.success(Unit))
	}
	
}