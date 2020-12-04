/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 19:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.server

import com.mmdev.me.driver.data.datasource.vehicle.server.dto.VehicleDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Write/read/update [VehicleDto] to Firestore backend
 */

interface IVehicleServerDataSource {
	
	/**
	 * Simply add new Vehicle entry that came from current device to server
	 */
	fun addVehicle(email: String, vehicle: VehicleDto): Flow<SimpleResult<Unit>>
	
	/**
	 * Get only one vehicle that match given [vin]
	 */
	fun getVehicle(email: String, vin: String): Flow<SimpleResult<VehicleDto>>
	
	/**
	 * Download whole collection that contains vehicles documents
	 * Used only when user sign in and we want to fetch old data to device if such exists
	 */
	fun getAllVehicles(email: String): Flow<SimpleResult<List<VehicleDto>>>
	
	/**
	 * Deletes existing vehicle on server
	 */
	fun deleteVehicle(email: String, vin: String): Flow<SimpleResult<Void>>
	
}