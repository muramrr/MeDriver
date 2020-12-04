/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.server

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.core.base.datasource.server.BaseServerDataSource
import com.mmdev.me.driver.data.core.firebase.ServerOperation
import com.mmdev.me.driver.data.core.firebase.ServerOperationType.MAINTENANCE
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.maintenance.server.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.core.combineResultStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge

/**
 * [IMaintenanceServerDataSource] implementation
 */

class MaintenanceServerDataSourceImpl (private val fs: FirebaseFirestore) :
		BaseServerDataSource(fs), IMaintenanceServerDataSource{
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_VEHICLES_COLLECTION = "vehicles"
		private const val FS_VEHICLE_REPLACED_PARTS = "replaced_parts"
		private const val FS_DATE_FIELD = "date"
	}
	
	private fun toServerOperation(dto: VehicleSparePartDto): ServerOperation =
		ServerOperation(
			type = MAINTENANCE,
			vin = dto.vehicleVinCode,
			dateAdded = dto.dateAdded,
			documentId = dto.date
		)
	
	private fun add(email: String, vin: String, dto: VehicleSparePartDto) =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_VEHICLE_REPLACED_PARTS)
			.document(dto.date)
			.setAsFlow(dto)
	
	override fun addMaintenanceHistoryItems(
		email: String, vin: String, items: List<VehicleSparePartDto>
	): Flow<SimpleResult<Unit>> =
		items.asFlow().flatMapMerge(5) { dto ->
			add(email, vin, dto).combine(addToJournal(email, toServerOperation(dto))) { add, journal ->
				combineResultStates(add, journal).fold(
					success = {
						MedriverApp.lastOperationSyncedId = dto.dateAdded
						ResultState.success(Unit)
					},
					failure = {
						ResultState.failure(it)
					}
				)
			}
		}
		
	
	override fun getAllMaintenanceHistory(
		email: String, vin: String
	): Flow<SimpleResult<List<VehicleSparePartDto>>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_VEHICLE_REPLACED_PARTS)
			//.orderBy(FS_DATE_FIELD, Query.Direction.DESCENDING)
			.executeAndDeserializeAsFlow(VehicleSparePartDto::class.java)
	
	override fun getMaintenanceHistoryById(
		email: String, vin: String, id: String
	): Flow<SimpleResult<VehicleSparePartDto>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_VEHICLE_REPLACED_PARTS)
			.document(id)
			.getAndDeserializeAsFlow(VehicleSparePartDto::class.java)
	
	
	override fun deleteMaintenanceEntry(
		email: String, vin: String, id: String
	): Flow<SimpleResult<Void>>  =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_VEHICLE_REPLACED_PARTS)
			.document(id)
			.delete()
			.asFlow()
}