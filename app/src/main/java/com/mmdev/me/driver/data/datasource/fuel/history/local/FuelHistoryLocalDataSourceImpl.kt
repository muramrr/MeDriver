/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 16:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.local

import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IFuelHistoryLocalDataSource] implementation
 */

class FuelHistoryLocalDataSourceImpl(
	private val dao: FuelHistoryDao,
	private val cache: CacheDao
): BaseDataSource(), IFuelHistoryLocalDataSource {
	
	override suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit> =
		safeCall(TAG) { cache.insertOperation(cachedOperation) }.also {
			logWarn(TAG, "Something doesn't require to write to backend, caching operation:$cachedOperation")
		}
	
	override suspend fun getCachedOperations(): SimpleResult<List<CachedOperation>> = safeCall(TAG) {
		cache.getPendingOperations(MeDriverRoomDatabase.FUEL_HISTORY_TABLE)
	}
	
	override suspend fun deleteCachedOperation(cachedOperation: CachedOperation): SimpleResult<Unit> =
		safeCall(TAG) { cache.deleteOperation(cachedOperation) }.also {
			logInfo(TAG, "Deleting operation: $cachedOperation")
		}
	
	override suspend fun getFuelHistory(
		vin: String, limit: Int, offset: Int
	): SimpleResult<List<FuelHistoryEntity>> = safeCall(TAG) {
		dao.getVehicleFuelHistory(vin, limit, offset)
	}
	
	override suspend fun getFirstFuelHistoryEntry(vin: String): SimpleResult<FuelHistoryEntity?> =
		safeCall(TAG) { dao.getVehicleFuelHistoryFirst(vin) }
	
	override suspend fun getRecordById(key: Long): SimpleResult<FuelHistoryEntity?> =
		safeCall(TAG) { dao.getRecordById(key) }
	
	override suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit> =
		safeCall(TAG) { dao.insertFuelHistoryEntity(fuelHistoryEntity) }.also {
			logDebug(TAG, "Adding History entry: " +
			              "id = ${fuelHistoryEntity.date}, " +
			              "date = ${convertToLocalDateTime(fuelHistoryEntity.date).date}")
		}
	
	override suspend fun importFuelHistory(import: List<FuelHistoryEntity>): SimpleResult<Unit> =
		safeCall(TAG) { import.forEach { dao.insertFuelHistoryEntity(it) }}
	
	override suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit> =
		safeCall(TAG) { dao.deleteFuelHistoryEntity(fuelHistoryEntity) }.also {
			logDebug(TAG, "Deleting History entry: id = ${fuelHistoryEntity.date}")
		}
	
	override suspend fun clearAll(): SimpleResult<Unit> = safeCall(TAG) { dao.clearHistory() }
	
}