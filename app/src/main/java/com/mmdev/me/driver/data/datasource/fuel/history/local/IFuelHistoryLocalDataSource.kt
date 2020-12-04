/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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