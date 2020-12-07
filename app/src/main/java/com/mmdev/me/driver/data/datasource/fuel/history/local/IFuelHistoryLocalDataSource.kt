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

package com.mmdev.me.driver.data.datasource.fuel.history.local

import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.core.base.datasource.caching.IBaseLocalDataSourceWithCaching
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 *  Wrapper for [com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao]
 */

interface IFuelHistoryLocalDataSource: IBaseLocalDataSourceWithCaching {
	
	/**
	 * Retrieve fuel history with given [limit] and [offset] to SQL query
	 * These params used to simulate paging (controls from repository)
	 */
	suspend fun getFuelHistory(vin: String, limit: Int, offset: Int): SimpleResult<List<FuelHistoryEntity>>
	
	/**
	 * Used primary with cached operations logic
	 * [CachedOperation] class contains entry id and this method is designed for retrieving only
	 * 1 entry by its id
	 */
	suspend fun getRecordById(key: Long): SimpleResult<FuelHistoryEntity?>
	
	/**
	 * Load only last fuel history entry
	 * Used when user wants to add new history entry and we can autocomplete some input fields
	 * based on last one added entry
	 */
	suspend fun getFirstFuelHistoryEntry(vin: String): SimpleResult<FuelHistoryEntity?>
	
	/**
	 * Simple usage - insert new fuel history entry
	 * Can be called when new data comes from server notifications or
	 * it has been added from device
	 */
	suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit>
	
	/**
	 * Import a pack of [FuelHistoryEntity] entries
	 * Basically used only when user signs in and we want to download all data if such exists
	 * and store it into a cache
	 */
	suspend fun importFuelHistory(import: List<FuelHistoryEntity>): SimpleResult<Unit>
	
	/**
	 * Delete only one history entry
	 */
	suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit>
	
	/**
	 * Clear whole table that contains [FuelHistoryEntity]
	 */
	suspend fun clearAll(): SimpleResult<Unit>
	
}