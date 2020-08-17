/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.history

import com.mmdev.me.driver.core.utils.logDebug
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.data.datasource.local.fuel.history.entities.FuelHistoryEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IFuelHistoryLocalDataSource] implementation
 */

internal class FuelHistoryLocalDataSourceImpl(private val fuelDao: FuelDao) :
		BaseDataSource(), IFuelHistoryLocalDataSource {
	
	
	override suspend fun getFuelHistory(limit: Int, offset: Int): SimpleResult<List<FuelHistoryEntity>> =
		safeCall { fuelDao.getFuelHistory(limit, offset) }
	
	override suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity) =
		fuelDao.insertFuelHistoryRecord(fuelHistoryEntity).also {
			logDebug(TAG, "Adding History entry: id = ${fuelHistoryEntity.historyEntryId}")
		}
	
	override suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity) =
		fuelDao.deleteFuelHistorySingleRecord(fuelHistoryEntity).also {
			logDebug(TAG, "Deleting History entry: id = ${fuelHistoryEntity.historyEntryId}")
		}
}