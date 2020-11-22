/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.maintenance.remote.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge

/**
 * [IMaintenanceRemoteDataSource] implementation
 */

class MaintenanceRemoteDataSourceImpl (private val fs: FirebaseFirestore) :
		IMaintenanceRemoteDataSource, BaseDataSource() {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_VEHICLES_COLLECTION = "vehicles"
		private const val FS_VEHICLE_REPLACED_PARTS = "replaced_parts"
		private const val FS_DATE_FIELD = "date"
	}
	
	override fun addMaintenanceHistoryItems(
		email: String, vin: String, items: List<VehicleSparePartDto>
	): Flow<SimpleResult<Unit>> =
		items.asFlow().flatMapMerge(5) { dto ->
			fs.collection(FS_USERS_COLLECTION)
				.document(email)
				.collection(FS_VEHICLES_COLLECTION)
				.document(vin)
				.collection(FS_VEHICLE_REPLACED_PARTS)
				.document(dto.date)
				.setAsFlow(dto)
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
	
	
	override fun updateMaintenanceEntryField(
		email: String, vin: String, id: String, field: String, value: Any
	): Flow<SimpleResult<Void>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_VEHICLES_COLLECTION)
			.document(vin)
			.collection(FS_VEHICLE_REPLACED_PARTS)
			.document(id)
			.update(field, value)
			.asFlow()
}