/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.11.2020 15:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.maintenance

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.user.UserDataInfo
import kotlinx.coroutines.flow.Flow

/**
 *
 */

interface IMaintenanceRepository {
	
	suspend fun addMaintenanceItems(
		user: UserDataInfo?,
		items: List<VehicleSparePart>
	): Flow<SimpleResult<Unit>>
	
	suspend fun findLastReplaced(
		vin: String,
		systemNode: String,
		customComponent: String
	): SimpleResult<VehicleSparePart>
	
	suspend fun getMaintenanceHistory(
		vin: String,
		size: Int?,
	): SimpleResult<List<VehicleSparePart>>
	
	suspend fun getSystemNodeHistory(
		vin: String,
		systemNode: String
	): SimpleResult<List<VehicleSparePart>>
	
	suspend fun getHistoryByTypedQuery(
		vin: String,
		typedQuery: String
	): SimpleResult<List<VehicleSparePart>>
	
}