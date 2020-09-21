/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 16:51
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
	
	fun addVehicle(email: String, vehicle: VehicleDto): Flow<SimpleResult<Unit>>
	
	fun getVehicle(email: String, vin: String): Flow<SimpleResult<VehicleDto>>
	
	fun getAllVehicles(email: String): Flow<SimpleResult<List<VehicleDto>>>
	
	fun updateVehicleField(
		email: String, vin: String, field: String, value: Any
	): Flow<SimpleResult<Void>>
	
}