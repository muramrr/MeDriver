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

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.core.base.datasource.caching.BaseLocalDataSourceWithCaching
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IFuelHistoryLocalDataSource] implementation
 */

class FuelHistoryLocalDataSourceImpl(
	private val dao: FuelHistoryDao, cache: CacheDao
): BaseLocalDataSourceWithCaching(cache), IFuelHistoryLocalDataSource {
	
	override val table: String
		get() = MeDriverRoomDatabase.FUEL_HISTORY_TABLE
	
	override suspend fun getFuelHistory(
		vin: String, limit: Int, offset: Int
	): SimpleResult<List<FuelHistoryEntity>> =
		safeCall(TAG) { dao.getVehicleFuelHistory(vin, limit, offset) }
	
	override suspend fun getFirstFuelHistoryEntry(vin: String): SimpleResult<FuelHistoryEntity?> =
		safeCall(TAG) { dao.getVehicleFuelHistoryFirst(vin) }
	
	override suspend fun getRecordById(key: Long): SimpleResult<FuelHistoryEntity?> =
		safeCall(TAG) { dao.getRecordById(key) }
	
	override suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit> =
		safeCall(TAG) { dao.insertFuelHistoryEntity(fuelHistoryEntity) }.also {
			logDebug(TAG, "Adding History entry: id = ${fuelHistoryEntity.dateAdded}")
		}
	
	override suspend fun importFuelHistory(import: List<FuelHistoryEntity>): SimpleResult<Unit> =
		safeCall(TAG) { dao.importFuelHistory(import) }.also {
			// if list is not empty -> update lastOperationSynced
			import.maxByOrNull { it.dateAdded }?.dateAdded?.let {
				MedriverApp.lastOperationSyncedId = it
			}
			import.forEach {
				logDebug(TAG, "Importing History entry: id = ${it.dateAdded}")
			}
		}
	
	override suspend fun deleteFuelHistoryEntry(id: Long): SimpleResult<Unit> =
		safeCall(TAG) { dao.deleteFuelHistoryEntity(id) }.also {
			deleteCachedOperationById(id.toString())
			logDebug(TAG, "Deleting History entry: id = $id")
		}
	
	override suspend fun clearAll(): SimpleResult<Unit> = safeCall(TAG) { dao.clearHistory() }
	
}