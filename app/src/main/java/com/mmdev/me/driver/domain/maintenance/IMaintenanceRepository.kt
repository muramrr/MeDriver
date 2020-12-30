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
 * Repository provides data for [com.mmdev.me.driver.presentation.ui.maintenance]
 */

interface IMaintenanceRepository {
	
	/**
	 * Add [VehicleSparePart] record to database and (if possible) send data to server
	 * Otherwise remember this operation by adding it to operations cache
	 */
	fun addMaintenanceItems(
		user: UserDataInfo?,
		items: List<VehicleSparePart>
	): Flow<SimpleResult<Unit>>
	
	/**
	 * Find the [VehicleSparePart] with same identifiers to know when if it was replaced before
	 */
	suspend fun findLastReplaced(
		vin: String,
		systemNode: VehicleSystemNodeType,
		component: SparePart
	): SimpleResult<VehicleSparePart>
	
	/**
	 * Used for pagination
	 * Load first items
	 */
	suspend fun getInitMaintenanceHistory(vin: String): SimpleResult<List<VehicleSparePart>>
	
	/**
	 * Used for pagination
	 * Seems that we already initiated first items -> Load more
	 */
	suspend fun getMoreMaintenanceHistory(vin: String): SimpleResult<List<VehicleSparePart>>
	
	/**
	 * Used for pagination
	 * Seems that we already initiated and loaded more
	 * User scrolled back? Load previous
	 */
	suspend fun getPreviousMaintenanceHistory(vin: String): SimpleResult<List<VehicleSparePart>>
	
	/**
	 * Filter [VehicleSparePart] for param
	 * @param systemNode defines [VehicleSystemNodeType] for which we should retrieve all [VehicleSparePart]
	 */
	suspend fun getSystemNodeHistory(
		vin: String,
		systemNode: VehicleSystemNodeType
	): SimpleResult<List<VehicleSparePart>>
	
	/**
	 * User typed in search view some text? Find it and return list which matches given query
	 */
	suspend fun getHistoryByTypedQuery(vin: String, typedQuery: String): SimpleResult<List<VehicleSparePart>>
	
	/**
	 * Delete [VehicleSparePart] entry from database and (if possible) from server
	 */
	fun removeMaintenanceEntry(user: UserDataInfo?, maintenance: VehicleSparePart): Flow<SimpleResult<Unit>>
}