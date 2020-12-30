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

package com.mmdev.me.driver.data.datasource.vehicle.server

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.data.core.base.datasource.server.BaseServerDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.fetching.data.ServerDocumentType.VEHICLE
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperation
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperationType
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperationType.ADDED
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperationType.DELETED
import com.mmdev.me.driver.data.datasource.vehicle.server.dto.VehicleDto
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.ResultState.Companion.toUnit
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.core.combineResultStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * [IVehicleServerDataSource] implementation
 */

class VehicleServerDataSourceImpl(
	private val fs: FirebaseFirestore
): BaseServerDataSource(fs), IVehicleServerDataSource {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_VEHICLES_COLLECTION = "vehicles"
		
		private const val FS_FUEL_HISTORY_COLLECTION = "fuel_history"
		private const val FS_VEHICLE_REPLACED_PARTS = "replaced_parts"
		private const val BATCH_SIZE = 10L
	}
	
	private fun toServerOperation(dto: VehicleDto, type: ServerOperationType): ServerOperation =
		ServerOperation(
			operationType = type,
			vin = dto.vin,
			dateAdded = if (type == DELETED) currentEpochTime() else dto.dateUpdated,
			documentId = dto.vin,
			documentType = VEHICLE,
		)
	
	private fun add(email: String, vehicle: VehicleDto): Flow<SimpleResult<Unit>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vehicle.vin)
			.setAsFlow(vehicle)
	
	override fun addVehicle(email: String, vehicle: VehicleDto): Flow<SimpleResult<Unit>> =
		add(email, vehicle).combine(addOperationToJournal(email, toServerOperation(vehicle, ADDED))) { add, journal ->
			combineResultStates(add, journal).fold(
				success = {
					MedriverApp.lastOperationSyncedId = vehicle.dateUpdated
					ResultState.success(Unit)
				},
				failure = {
					ResultState.failure(it)
				}
			)
		}
	
	
	override fun getVehicle(email: String, vin: String): Flow<SimpleResult<VehicleDto>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.getAndDeserializeAsFlow(VehicleDto::class.java)
	
	override fun getAllVehicles(email: String): Flow<SimpleResult<List<VehicleDto>>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.executeAndDeserializeAsFlow(VehicleDto::class.java)
	
	override fun deleteVehicle(email: String, dto: VehicleDto): Flow<SimpleResult<Unit>> =
		delete(email, dto.vin)
			.combine(addOperationToJournal(email, toServerOperation(dto, DELETED))) { delete, journal ->
				combineResultStates(delete, journal).fold(
					success = {
						MedriverApp.lastOperationSyncedId = dto.dateUpdated
						ResultState.success(Unit)
					},
					failure = { ResultState.failure(it) }
				)
			}
	
	private fun delete(email: String, vin: String): Flow<SimpleResult<Unit>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.delete()
			.asFlow()
			.map {
				// it is unsafe calling from android client
				deleteFuelHistory(email, vin)
				deleteMaintenance(email, vin)
				it.toUnit()
			}
	
	/* https://firebase.google.com/docs/firestore/manage-data/delete-data#java_5 */
	private suspend fun deleteFuelHistory(email: String, vin: String) {
		var deleted = 0
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_FUEL_HISTORY_COLLECTION)
			.limit(BATCH_SIZE)
			.get()
			.asFlow()
			.collect { result ->
				result.fold(
					success = { querySnapshot ->
						logWtf(TAG, "deleting fuel history snapshot = $querySnapshot")
						querySnapshot.documents.forEach {
							it.reference.delete()
							++deleted
						}
						if (deleted >= BATCH_SIZE) {
							// retrieve and delete another batch
							deleteFuelHistory(email, vin)
						}
					},
					failure = {
						logError(TAG, "$it")
					}
				)
			}
	}
	
	/* https://firebase.google.com/docs/firestore/manage-data/delete-data#java_5 */
	private suspend fun deleteMaintenance(email: String, vin: String) {
		var deleted = 0
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_VEHICLE_REPLACED_PARTS)
			.limit(BATCH_SIZE)
			.get()
			.asFlow()
			.collect { result ->
				result.fold(
					success = { querySnapshot ->
						logWtf(TAG, "deleting maintenance snapshot = $querySnapshot")
						querySnapshot.documents.forEach {
							it.reference.delete()
							++deleted
						}
						if (deleted >= BATCH_SIZE) {
							// retrieve and delete another batch
							deleteMaintenance(email, vin)
						}
					},
					failure = {
						logError(TAG, "$it")
					}
				)
			}
	}
	
	
	override fun deleteFromJournal(email: String, id: String): Flow<SimpleResult<Unit>> =
		deleteOperationFromJournal(email, VEHICLE, id)
	
	
}