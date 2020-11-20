/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.11.2020 21:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local

import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 *
 */

interface IVehicleLocalDataSource {
	
	suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit>
	suspend fun getCachedOperations(): SimpleResult<List<CachedOperation>>
	suspend fun deleteCachedOperation(cachedOperation: CachedOperation): SimpleResult<Unit>
	
	suspend fun gePlannedReplacements(vin: String): SimpleResult<Map<String, VehicleSparePartEntity>>
	
	suspend fun getAllVehicles(): SimpleResult<List<VehicleEntity>>
	suspend fun getVehicle(vin: String): SimpleResult<VehicleEntity>
	suspend fun insertVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit>
	suspend fun deleteVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit>
	
	suspend fun clearAll(): SimpleResult<Unit>
	
}