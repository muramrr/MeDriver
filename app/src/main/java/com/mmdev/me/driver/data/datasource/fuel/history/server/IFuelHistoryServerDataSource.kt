/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 19:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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