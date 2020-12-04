/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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