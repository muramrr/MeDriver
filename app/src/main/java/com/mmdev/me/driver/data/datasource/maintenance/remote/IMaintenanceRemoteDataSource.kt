/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 15:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.remote

import com.mmdev.me.driver.data.datasource.maintenance.remote.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * This datasource responsible to read/write data from/to backend (backup purposes)
 *
 * @param email identifies user in users collection
 * @param vin identifies car in user cars collection
 */

interface IMaintenanceRemoteDataSource {
	
	fun addMaintenanceHistoryItems(
		email: String,
		vin: String,
		items: List<VehicleSparePartDto>
	): Flow<SimpleResult<Unit>>
	
	fun getMaintenanceHistory(
		email: String, vin: String
	): Flow<SimpleResult<List<VehicleSparePartDto>>>
	
	fun updateMaintenanceEntryField(
		email: String,
		vin: String,
		documentId: String,
		field: String,
		value: Any
	): Flow<SimpleResult<Void>>
	
}