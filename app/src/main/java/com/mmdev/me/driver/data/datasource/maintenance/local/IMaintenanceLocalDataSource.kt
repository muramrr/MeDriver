/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 18:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.local

import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Wrapper for [com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao]
 */

interface IMaintenanceLocalDataSource {
	
	suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit>
	suspend fun cachePendingWriteToBackend(cachedOperations: List<CachedOperation>): SimpleResult<Unit>
	
	suspend fun findLastReplaced(
		vin: String,
		systemNode: String,
		customNodeComponent: String
	): SimpleResult<VehicleSparePartEntity>
	
	suspend fun getMaintenanceHistory(
		vin: String,
		limit: Int,
		offset: Int
	): SimpleResult<List<VehicleSparePartEntity>>
	
	
	suspend fun getByTypedQuery(
		vin: String,
		typedQuery: String
	): SimpleResult<List<VehicleSparePartEntity>>
	
	suspend fun getSystemNodeHistory(
		vin: String,
		systemNode: String
	): SimpleResult<List<VehicleSparePartEntity>>
	
	suspend fun insertReplacedSpareParts(
		replacedSpareParts: List<VehicleSparePartEntity>
	): SimpleResult<Unit>
	
	suspend fun updateReplacedSparePart(
		replacedSparePart: VehicleSparePartEntity
	): SimpleResult<Unit>
	
	suspend fun deleteFuelHistoryEntry(replacedSparePart: VehicleSparePartEntity): SimpleResult<Unit>
	
	suspend fun clearAll(): SimpleResult<Unit>
	
}