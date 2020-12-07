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