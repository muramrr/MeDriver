/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 18:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.local

import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 *  Wrapper for [com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao]
 */

interface IFuelHistoryLocalDataSource {
	
	suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit>
	suspend fun getFuelHistory(vin: String, limit: Int, offset: Int): SimpleResult<List<FuelHistoryEntity>>
	suspend fun getFirstFuelHistoryEntry(vin: String): SimpleResult<FuelHistoryEntity?>
	suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit>
	suspend fun importFuelHistory(import: List<FuelHistoryEntity>): SimpleResult<Unit>
	suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit>
	suspend fun clearAll(): SimpleResult<Unit>
	
}