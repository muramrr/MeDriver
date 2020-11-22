/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.local

import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Wrapper for [com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao]
 */

interface IMaintenanceLocalDataSource {
	
	/**
	 * If writing to backend cannot be done at the moment - we will remember need id and write it
	 * to cached operations table
	 * Another time we will try to fetch all table entries to server again
	 */
	suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit>
	
	/**
	 * Same as [cachePendingWriteToBackend] but uses list as a parameter
	 */
	suspend fun cachePendingWriteToBackend(cachedOperations: List<CachedOperation>): SimpleResult<Unit>
	
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
	 * Used when user is adding a new component replacement and if such component has already been
	 * replaced at some time - find this entry in database and show it to user
	 */
	suspend fun findLastReplaced(
		vin: String,
		systemNode: String,
		customNodeComponent: String
	): SimpleResult<VehicleSparePartEntity>
	
	/**
	 * Retrieve maintenance history with given [limit] and [offset] to SQL query
	 * These params used to simulate paging (controls from repository)
	 */
	suspend fun getMaintenanceHistory(
		vin: String,
		limit: Int,
		offset: Int
	): SimpleResult<List<VehicleSparePartEntity>>
	
	
	/**
	 * Used primary with cached operations logic
	 * [CachedOperation] class contains entry id and this method is designed for retrieving only
	 * 1 entry by its id
	 */
	suspend fun getRecordById(key: Long): SimpleResult<VehicleSparePartEntity?>
	
	/**
	 * Retrieve all maintenance entries which fits given query
	 * Query comes from Ui SearchView input
	 */
	suspend fun getByTypedQuery(
		vin: String,
		typedQuery: String
	): SimpleResult<List<VehicleSparePartEntity>>
	
	/**
	 * Retrieve all maintenance entries which relates to given [systemNode]
	 * Query comes from UI dialog chooser
	 */
	suspend fun getSystemNodeHistory(
		vin: String,
		systemNode: String
	): SimpleResult<List<VehicleSparePartEntity>>
	
	/**
	 * Simple usage - insert new maintenance entry
	 * Can be called when new data comes from server notifications or
	 * it has been added from device
	 */
	suspend fun insertReplacedSpareParts(
		replacedSpareParts: List<VehicleSparePartEntity>
	): SimpleResult<Unit>
	
	/**
	 * Delete only one history entry
	 */
	suspend fun deleteFuelHistoryEntry(replacedSparePart: VehicleSparePartEntity): SimpleResult<Unit>
	
	/**
	 * Clear whole table that contains [VehicleSparePartEntity]
	 */
	suspend fun clearAll(): SimpleResult<Unit>
	
}