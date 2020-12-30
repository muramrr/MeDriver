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
	 * @param dto indicates entry in collection to be deleted
	 */
	fun deleteMaintenanceEntry(email: String, dto: VehicleSparePartDto): Flow<SimpleResult<Unit>>
	
	
	/**
	 * Delete entry from journal
	 * It is pretty useful when user trying to fetch data which is no longer available
	 * Neither catching "No such document." exceptions - better to delete entry from journal
	 */
	fun deleteFromJournal(email: String, id: String): Flow<SimpleResult<Unit>>
	
}