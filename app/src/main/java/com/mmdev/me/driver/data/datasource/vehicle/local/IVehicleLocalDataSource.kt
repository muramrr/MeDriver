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

import com.mmdev.me.driver.data.core.base.datasource.caching.IBaseLocalDataSourceWithCaching
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.MaintenanceEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.vehicle.data.Expenses

/**
 * Wrapper for [com.mmdev.me.driver.data.datasource.vehicle.local.dao.VehicleDao]
 */

interface IVehicleLocalDataSource: IBaseLocalDataSourceWithCaching {
	
	/**
	 * Get expenses which related to vehicle specified by [vin]
	 */
	suspend fun getExpenses(vin: String): SimpleResult<Expenses>
	
	/**
	 * Get all replacement parts for given vehicle [vin] which was done before
	 */
	suspend fun gePlannedReplacements(vin: String): SimpleResult<Map<String, MaintenanceEntity>>
	
	/**
	 * Get all vehicles which was added to database
	 * Used to choose a vehicle from UI drop list component
	 */
	suspend fun getAllVehicles(): SimpleResult<List<VehicleEntity>>
	
	/**
	 * Get vehicle which corresponds to given [vin]
	 */
	suspend fun getVehicle(vin: String): SimpleResult<VehicleEntity?>
	
	/**
	 * Simple usage - insert new vehicle entry
	 * Can be called when new data comes from server notifications or
	 * it has been added from device
	 */
	suspend fun insertVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit>
	
	/**
	 * Same as [insertVehicle], but implementation differs
	 * Used only to download data and import it to database
	 */
	suspend fun importVehicles(import: List<VehicleEntity>): SimpleResult<Unit>
	
	/**
	 * Delete only one vehicle
	 */
	suspend fun deleteVehicle(vin: String): SimpleResult<Unit>
	
	/**
	 * Clear whole table that contains [VehicleEntity]
	 */
	suspend fun clearAll(): SimpleResult<Unit>
	
}