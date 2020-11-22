/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.remote

import com.mmdev.me.driver.data.datasource.vehicle.remote.dto.VehicleDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Write/read/update [VehicleDto] to Firestore backend
 */

interface IVehicleRemoteDataSource {
	
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
	 * Update existing vehicle on server
	 *
	 * @param field indicates field which need to update
	 * @param value indicates new value which need to store on server
	 */
	fun updateVehicleField(
		email: String, vin: String, field: String, value: Any
	): Flow<SimpleResult<Void>>
	
}