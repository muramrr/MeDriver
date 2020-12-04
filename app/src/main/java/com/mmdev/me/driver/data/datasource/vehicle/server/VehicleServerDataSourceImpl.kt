/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.server

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.core.base.datasource.server.BaseServerDataSource
import com.mmdev.me.driver.data.core.firebase.ServerOperation
import com.mmdev.me.driver.data.core.firebase.ServerOperationType.VEHICLE
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.vehicle.server.dto.VehicleDto
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.core.combineResultStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * [IVehicleServerDataSource] implementation
 */

class VehicleServerDataSourceImpl(
	private val fs: FirebaseFirestore
): BaseServerDataSource(fs), IVehicleServerDataSource {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_VEHICLES_COLLECTION = "vehicles"
	}
	
	private fun toServerOperation(dto: VehicleDto): ServerOperation =
		ServerOperation(
			type = VEHICLE,
			vin = dto.vin,
			dateAdded = dto.dateUpdated,
			documentId = dto.vin
		)
	
	private fun add(email: String, vehicle: VehicleDto): Flow<SimpleResult<Unit>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vehicle.vin)
			.setAsFlow(vehicle)
	
	override fun addVehicle(email: String, vehicle: VehicleDto): Flow<SimpleResult<Unit>> =
		add(email, vehicle).combine(addToJournal(email, toServerOperation(vehicle))) { add, journal ->
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
	
	override fun deleteVehicle(email: String, vin: String): Flow<SimpleResult<Void>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.delete()
			.asFlow()
}