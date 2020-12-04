/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.local

import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.core.base.datasource.caching.IBaseLocalDataSourceWithCaching
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Wrapper for [com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao]
 */

interface IMaintenanceLocalDataSource: IBaseLocalDataSourceWithCaching {
	
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
	 * Same as [insertReplacedSpareParts]
	 * Used only while downloading new data, implementation differs from [insertReplacedSpareParts]
	 */
	suspend fun importReplacedSpareParts(
		import: List<VehicleSparePartEntity>
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