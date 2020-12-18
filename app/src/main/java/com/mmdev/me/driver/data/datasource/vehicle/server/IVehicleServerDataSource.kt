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
	fun deleteVehicle(email: String, dto: VehicleDto): Flow<SimpleResult<Unit>>
	
}