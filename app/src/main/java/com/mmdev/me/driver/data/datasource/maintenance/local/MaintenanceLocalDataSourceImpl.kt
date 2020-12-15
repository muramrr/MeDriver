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

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.core.base.datasource.caching.BaseLocalDataSourceWithCaching
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IMaintenanceLocalDataSource] implementation
 */

class MaintenanceLocalDataSourceImpl(
	private val dao: MaintenanceDao,
	cache: CacheDao
): BaseLocalDataSourceWithCaching(cache), IMaintenanceLocalDataSource  {
	
	override val table: String
		get() = MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE
	
	override suspend fun findLastReplaced(
		vin: String, systemNode: String, customNodeComponent: String
	): SimpleResult<VehicleSparePartEntity> = safeCall(TAG) {
		dao.findLastReplaced(vin, systemNode, customNodeComponent)
	}
	
	override suspend fun getMaintenanceHistory(
		vin: String, limit: Int, offset: Int
	): SimpleResult<List<VehicleSparePartEntity>> = safeCall(TAG) {
		dao.getMaintenanceHistory(vin, limit, offset)
	}
	
	override suspend fun getRecordById(key: Long): SimpleResult<VehicleSparePartEntity?> =
		safeCall(TAG) { dao.getById(key) }
	
	override suspend fun getByTypedQuery(
		vin: String, typedQuery: String
	): SimpleResult<List<VehicleSparePartEntity>> = safeCall(TAG) {
		dao.getByTypedQuery(vin, "%$typedQuery%") //due to specific room query syntax
	}
	
	override suspend fun getSystemNodeHistory(
		vin: String, systemNode: String
	): SimpleResult<List<VehicleSparePartEntity>> = safeCall(TAG) {
		dao.getSystemNodeHistory(vin, systemNode)
	}
	
	override suspend fun insertReplacedSpareParts(
		replacedSpareParts: List<VehicleSparePartEntity>
	): SimpleResult<Unit> = safeCall(TAG) { dao.insertVehicleReplacedSparePart(replacedSpareParts) }.also {
		replacedSpareParts.forEach {
			logDebug(TAG,
				"Adding Replaced spare part: " + "id = ${it.dateAdded}, " +
				"date = ${it.date}, " +
				"part vendor and articulus = ${it.vendor}, ${it.articulus}, " +
				"parent = ${it.systemNode} child = ${it.systemNodeComponent} criteria = ${it.searchCriteria}"
			)
		}
	}
	
	override suspend fun importReplacedSpareParts(
		import: List<VehicleSparePartEntity>
	): SimpleResult<Unit> = safeCall(TAG) { dao.insertVehicleReplacedSparePart(import) }.also {
		// if list is not empty -> update lastOperationSynced
		import.maxByOrNull { it.dateAdded }?.dateAdded?.let {
			MedriverApp.lastOperationSyncedId = it
		}
		import.forEach {
			logDebug(TAG,
			         "Importing Replaced spare part: id = ${it.dateAdded}, " +
			         "date = ${it.date}, " +
			         "part vendor and articulus = ${it.vendor}, ${it.articulus}, " +
			         "parent = ${it.systemNode} child = ${it.systemNodeComponent} criteria = ${it.searchCriteria}"
			)
		}
	}
	
	override suspend fun deleteMaintenanceHistoryEntry(id: Long): SimpleResult<Unit> = safeCall(TAG) {
		dao.deleteVehicleReplacedSparePart(id)
	}.also {
		deleteCachedOperationById(id.toString())
		logDebug(TAG, "Deleting Replaced spare part entry: id = $id")
	}
	
	override suspend fun clearAll(): SimpleResult<Unit> = safeCall(TAG) { dao.clearHistory() }
	
}