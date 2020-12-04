/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local

import com.mmdev.me.driver.data.core.base.datasource.caching.IBaseLocalDataSourceWithCaching
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
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
	suspend fun gePlannedReplacements(vin: String): SimpleResult<Map<String, VehicleSparePartEntity>>
	
	/**
	 * Get all vehicles which was added to database
	 * Used to choose a vehicle from UI drop list component
	 */
	suspend fun getAllVehicles(): SimpleResult<List<VehicleEntity>>
	
	/**
	 * Get vehicle which corresponds to given [vin]
	 */
	suspend fun getVehicle(vin: String): SimpleResult<VehicleEntity>
	
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
	suspend fun deleteVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit>
	
	/**
	 * Clear whole table that contains [VehicleEntity]
	 */
	suspend fun clearAll(): SimpleResult<Unit>
	
}