/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local

import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.Expenses
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Wrapper for [com.mmdev.me.driver.data.datasource.vehicle.local.dao.VehicleDao]
 */

interface IVehicleLocalDataSource {
	
	/**
	 * If writing to backend cannot be done at the moment - we will remember need id and write it
	 * to cached operations table
	 * Another time we will try to fetch all table entries to server again
	 */
	suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit>
	
	/**
	 * Retrieve all cached operations from database
	 */
	suspend fun getCachedOperations(): SimpleResult<List<CachedOperation>>
	
	/**
	 * Delete cached operation
	 * There could be two reasons to delete operation:
	 * 1. Database entity was successfully written to server and we want to delete this from cached
	 * 2. Such entry doesn't exist in database
	 */
	suspend fun deleteCachedOperation(cachedOperation: CachedOperation): SimpleResult<Unit>
	
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
	 * Delete only one vehicle
	 */
	suspend fun deleteVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit>
	
	/**
	 * Clear whole table that contains [VehicleEntity]
	 */
	suspend fun clearAll(): SimpleResult<Unit>
	
}