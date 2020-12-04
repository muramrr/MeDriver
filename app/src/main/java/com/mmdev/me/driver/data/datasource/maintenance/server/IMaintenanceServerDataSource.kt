/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 19:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.server

import com.mmdev.me.driver.data.datasource.maintenance.server.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * This datasource responsible to read/write data from/to backend (backup purposes)
 *
 * @param email identifies user in users collection
 * @param vin identifies car in user cars collection
 */

interface IMaintenanceServerDataSource {
	
	/**
	 * Simply add new VehicleSparePart replacement entry that came from current device to server
	 */
	fun addMaintenanceHistoryItems(
		email: String,
		vin: String,
		items: List<VehicleSparePartDto>
	): Flow<SimpleResult<Unit>>
	
	/**
	 * Download whole collection that contains VehicleSparePart replacement documents
	 * Used only when user sign in and we want to fetch old data to device if such exists
	 */
	fun getAllMaintenanceHistory(
		email: String, vin: String
	): Flow<SimpleResult<List<VehicleSparePartDto>>>
	
	/**
	 * Download only one VehicleSparePart replacement document
	 * Used only to fetch new information that coming from firebase cloud messaging
	 * (New entry was added from another device, fetch that entry to current device)
	 *
	 * @param id indicates entry id in collection to be retrieved
	 */
	fun getMaintenanceHistoryById(
		email: String, vin: String, id: String
	): Flow<SimpleResult<VehicleSparePartDto>>
	
	
	/**
	 * Delete existing entry on server
	 *
	 * @param id indicates entry id in collection to be deleted
	 */
	fun deleteMaintenanceEntry(email: String, vin: String, id: String): Flow<SimpleResult<Void>>
	
}