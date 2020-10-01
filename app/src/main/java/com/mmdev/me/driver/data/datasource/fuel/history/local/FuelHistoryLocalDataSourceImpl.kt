/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.10.2020 18:58
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
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * [IFuelHistoryLocalDataSource] implementation
 */

class FuelHistoryLocalDataSourceImpl(private val dao: FuelHistoryDao) :
		BaseDataSource(), IFuelHistoryLocalDataSource {
	
	
	override suspend fun getFuelHistory(
		vin: String, limit: Int, offset: Int
	): SimpleResult<List<FuelHistoryEntity>> =
		safeCall { dao.getVehicleFuelHistory(vin, limit, offset) }
	
	override suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit> =
		safeCall { dao.insertFuelHistoryEntity(fuelHistoryEntity) }.also {
			logDebug(TAG, "Adding History entry: " +
			              "id = ${fuelHistoryEntity.date}, " +
			              "date = ${Instant.fromEpochMilliseconds(fuelHistoryEntity.date)
				              .toLocalDateTime(TimeZone.currentSystemDefault()).date}")
		}
	
	override suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit> =
		safeCall { dao.deleteFuelHistoryEntity(fuelHistoryEntity) }.also {
			logDebug(TAG, "Deleting History entry: id = ${fuelHistoryEntity.date}")
		}
		
}