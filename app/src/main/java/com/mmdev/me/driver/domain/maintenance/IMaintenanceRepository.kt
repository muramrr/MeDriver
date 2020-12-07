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

package com.mmdev.me.driver.domain.maintenance

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
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
		systemNode: VehicleSystemNodeType,
		component: SparePart
	): SimpleResult<VehicleSparePart>
	
	suspend fun getInitMaintenanceHistory(
		vin: String
	): SimpleResult<List<VehicleSparePart>>
	
	suspend fun getMoreMaintenanceHistory(
		vin: String
	): SimpleResult<List<VehicleSparePart>>
	
	suspend fun getPreviousMaintenanceHistory(
		vin: String
	): SimpleResult<List<VehicleSparePart>>
	
	suspend fun getSystemNodeHistory(
		vin: String,
		systemNode: VehicleSystemNodeType
	): SimpleResult<List<VehicleSparePart>>
	
	suspend fun getHistoryByTypedQuery(
		vin: String,
		typedQuery: String
	): SimpleResult<List<VehicleSparePart>>
	
}