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

package com.mmdev.me.driver.data.datasource.vehicle.local

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.core.base.datasource.caching.BaseLocalDataSourceWithCaching
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.dao.VehicleDao
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.vehicle.data.Expenses

/**
 * [IVehicleLocalDataSource] implementation
 */

class VehicleLocalDataSourceImpl(
	private val dao: VehicleDao,
	cache: CacheDao
): BaseLocalDataSourceWithCaching(cache), IVehicleLocalDataSource {
	
	override val table: String
		get() = MeDriverRoomDatabase.VEHICLES_TABLE
	
	override suspend fun getExpenses(vin: String): SimpleResult<Expenses> =
		safeCall(TAG) { dao.getExpenses(vin) }
	
	
	override suspend fun gePlannedReplacements(vin: String): SimpleResult<Map<String, VehicleSparePartEntity>> =
		safeCall(TAG) { dao.getPlannedReplacements(vin) }
	
	override suspend fun getAllVehicles(): SimpleResult<List<VehicleEntity>> =
		safeCall(TAG) { dao.getAllVehicles() }
	
	override suspend fun getVehicle(vin: String): SimpleResult<VehicleEntity> =
		safeCall(TAG) { dao.getVehicleByVin(vin) }
	
	override suspend fun insertVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit> =
		safeCall(TAG) { dao.insertVehicle(vehicleEntity) }
	
	override suspend fun importVehicles(import: List<VehicleEntity>): SimpleResult<Unit> =
		safeCall(TAG) { dao.importVehicles(import) }.also {
			MedriverApp.lastOperationSyncedId = import.maxByOrNull { it.lastUpdatedDate }!!.lastUpdatedDate
			import.forEach {
				logDebug(TAG,
				         "Importing Vehicle: vin = ${it.vin}, " +
				         "dateUpdated = ${it.lastUpdatedDate}"
				)
			}
		}
	
	override suspend fun deleteVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit> =
		safeCall(TAG) { dao.deleteVehicle(vehicleEntity) }
	
	override suspend fun clearAll(): SimpleResult<Unit> = safeCall(TAG) { dao.clearAll() }
	
	
}