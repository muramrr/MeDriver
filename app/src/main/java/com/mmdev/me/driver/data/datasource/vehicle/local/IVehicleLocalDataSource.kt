/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 18:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local

import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 *
 */

interface IVehicleLocalDataSource {
	
	suspend fun getAllVehicles(): SimpleResult<List<VehicleEntity>>
	
	suspend fun getVehicle(vin: String): SimpleResult<VehicleEntity>
	
	suspend fun insertVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit>
	
	suspend fun deleteVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit>
	
	suspend fun deleteAll(): SimpleResult<Unit>
	
}