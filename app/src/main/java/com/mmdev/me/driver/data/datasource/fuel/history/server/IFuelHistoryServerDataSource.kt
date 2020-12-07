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

import com.mmdev.me.driver.data.datasource.fuel.history.server.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * This datasource responsible to read/write data from/to backend (backup purposes)
 *
 * @param email identifies user in users collection
 * @param vin identifies car in user cars collection
 */

interface IFuelHistoryServerDataSource {
	
	/**
	 * Simply add new FuelHistory entry that came from current device to server
	 */
	fun addFuelHistory(
		email: String,
		vin: String,
		dto: FuelHistoryDto
	): Flow<SimpleResult<Unit>>
	
	/**
	 * Download whole collection that contains FuelHistory documents
	 * Used only when user sign in and we want to fetch old data to device if such exists
	 */
	fun getAllFuelHistory(email: String, vin: String): Flow<SimpleResult<List<FuelHistoryDto>>>
	
	
	/**
	 * Download only one FuelHistory document
	 * Used only to fetch new information that coming from firebase cloud messaging
	 * (New entry was added from another device, fetch that entry to current device)
	 *
	 * @param id indicates entry id in collection to be retrieved
	 */
	fun getFuelHistoryById(email: String, vin: String, id: String): Flow<SimpleResult<FuelHistoryDto>>
	
	
	/**
	 * Delete existing entry on server
	 *
	 * @param id indicates entry id in collection to be deleted
	 */
	fun deleteFuelHistoryEntry(email: String, vin: String, id: String, ): Flow<SimpleResult<Void>>
	
}