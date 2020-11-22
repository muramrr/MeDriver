/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 01:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * [IFuelHistoryRemoteDataSource] implementation
 */

class FuelHistoryRemoteDataSourceImpl(private val fs: FirebaseFirestore) :
		BaseDataSource(), IFuelHistoryRemoteDataSource {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_VEHICLES_COLLECTION = "vehicles"
		private const val FS_FUEL_HISTORY_COLLECTION = "fuel_history"
		private const val FS_DATE_FIELD = "date"
	}
	
	override fun addFuelHistory(
		email: String, vin: String, dto: FuelHistoryDto
	): Flow<SimpleResult<Unit>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_FUEL_HISTORY_COLLECTION)
			.document(dto.date)
			.setAsFlow(dto)
			
	
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
	
	override fun updateFuelHistoryField(
		email: String, vin: String, id: String, field: String, value: Any
	): Flow<SimpleResult<Void>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_FUEL_HISTORY_COLLECTION)
			.document(id)
			.update(field, value)
			.asFlow()
	
}