/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 17:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.local

import com.mmdev.me.driver.core.utils.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IMaintenanceLocalDataSource] implementation
 */

class MaintenanceLocalDataSourceImpl(private val dao: MaintenanceDao) :
		IMaintenanceLocalDataSource, BaseDataSource() {
	
	override suspend fun getMaintenanceHistory(
		vin: String, limit: Int, offset: Int
	): SimpleResult<List<VehicleSparePartEntity>> =
		safeCall(TAG) { dao.getMaintenanceHistory(vin, limit, offset) }
	
	override suspend fun insertReplacedSparePart(
		replacedSparePart: VehicleSparePartEntity
	): SimpleResult<Unit> = safeCall(TAG) { dao.insertVehicleReplacedSparePart(replacedSparePart) }.also {
		logDebug(TAG, "Adding Replaced spare part: " +
		              "id = ${replacedSparePart.date}, " +
		              "date = ${convertToLocalDateTime(replacedSparePart.date).date}, " +
		              "part details = ${replacedSparePart.vendor}, ${replacedSparePart.articulus}")
	}
	
	override suspend fun deleteFuelHistoryEntry(
		replacedSparePart: VehicleSparePartEntity
	): SimpleResult<Unit> = safeCall(TAG) { dao.deleteVehicleReplacedSparePart(replacedSparePart) }.also {
		logDebug(TAG, "Deleting Replaced spare part entry: id = ${replacedSparePart.date}")
	}
	
}