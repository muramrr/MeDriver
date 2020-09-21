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

import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.vehicle.local.dao.VehicleDao
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IVehicleLocalDataSource] implementation
 */

class VehicleLocalDataSourceImpl (private val dao: VehicleDao):
		IVehicleLocalDataSource, BaseDataSource() {
	
	override suspend fun getAllVehicles(): SimpleResult<List<VehicleEntity>> =
		safeCall { dao.getAllVehicles() }
	
	override suspend fun getVehicle(vin: String): SimpleResult<VehicleEntity> =
		safeCall { dao.getVehicleByVin(vin) }
	
	override suspend fun insertVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit> =
		safeCall { dao.insertVehicle(vehicleEntity) }
	
	override suspend fun deleteVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit> =
		safeCall { dao.deleteVehicle(vehicleEntity) }
	
	override suspend fun deleteAll(): SimpleResult<Unit> =
		safeCall { dao.deleteAll() }
	
	
}