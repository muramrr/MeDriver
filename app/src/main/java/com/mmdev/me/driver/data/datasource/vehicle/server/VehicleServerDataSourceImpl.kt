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
import com.mmdev.me.driver.data.core.base.datasource.server.BaseServerDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.fetching.data.ServerDocumentType.VEHICLE
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperation
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
			documentType = VEHICLE,
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