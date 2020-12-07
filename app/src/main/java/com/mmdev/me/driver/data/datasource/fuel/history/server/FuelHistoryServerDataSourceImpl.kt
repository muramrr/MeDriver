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

package com.mmdev.me.driver.data.datasource.fuel.history.server

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.core.base.datasource.server.BaseServerDataSource
import com.mmdev.me.driver.data.core.firebase.ServerOperation
import com.mmdev.me.driver.data.core.firebase.ServerOperationType.FUEL_HISTORY
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.fuel.history.server.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.core.combineResultStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * [IFuelHistoryServerDataSource] implementation
 */

class FuelHistoryServerDataSourceImpl(
	private val fs: FirebaseFirestore
) : BaseServerDataSource(fs), IFuelHistoryServerDataSource {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_VEHICLES_COLLECTION = "vehicles"
		private const val FS_FUEL_HISTORY_COLLECTION = "fuel_history"
		private const val FS_DATE_FIELD = "date"
	}
	
	private fun toServerOperation(dto: FuelHistoryDto): ServerOperation =
		ServerOperation(
			type = FUEL_HISTORY,
			vin = dto.vehicleVinCode,
			dateAdded = dto.dateAdded,
			documentId = dto.date
		)
	
	private fun add(email: String, vin: String, dto: FuelHistoryDto) =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_FUEL_HISTORY_COLLECTION)
			.document(dto.date)
			.setAsFlow(dto)
	
	override fun addFuelHistory(
		email: String, vin: String, dto: FuelHistoryDto
	): Flow<SimpleResult<Unit>> =
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
			
	
	override fun getAllFuelHistory(email: String, vin: String):
			Flow<SimpleResult<List<FuelHistoryDto>>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_FUEL_HISTORY_COLLECTION)
			//.orderBy(FS_DATE_FIELD, Query.Direction.DESCENDING)
			.executeAndDeserializeAsFlow(FuelHistoryDto::class.java)
	
	override fun getFuelHistoryById(
		email: String, vin: String, id: String
	): Flow<SimpleResult<FuelHistoryDto>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_FUEL_HISTORY_COLLECTION)
			.document(id)
			.getAndDeserializeAsFlow(FuelHistoryDto::class.java)
	
	override fun deleteFuelHistoryEntry(
		email: String, vin: String, id: String
	): Flow<SimpleResult<Void>>  =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_FUEL_HISTORY_COLLECTION)
			.document(id)
			.delete()
			.asFlow()
	
}