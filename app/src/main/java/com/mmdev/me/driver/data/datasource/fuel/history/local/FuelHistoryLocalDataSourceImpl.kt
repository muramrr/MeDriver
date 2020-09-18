/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.09.2020 17:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.local

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IFuelHistoryLocalDataSource] implementation
 */

internal class FuelHistoryLocalDataSourceImpl(private val dao: FuelHistoryDao) :
		BaseDataSource(), IFuelHistoryLocalDataSource {
	
	
	override suspend fun getFuelHistory(limit: Int, offset: Int): SimpleResult<List<FuelHistoryEntity>> =
		safeCall { dao.getFuelHistory(limit, offset) }
	
	override suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit> =
		safeCall { dao.insertFuelHistoryEntity(fuelHistoryEntity) }.also {
			logDebug(TAG, "Adding History entry: id = ${fuelHistoryEntity.historyEntryId}")
		}
	
	override suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit> =
		safeCall { dao.deleteFuelHistoryEntity(fuelHistoryEntity) }.also {
			logDebug(TAG, "Deleting History entry: id = ${fuelHistoryEntity.historyEntryId}")
		}
		
}