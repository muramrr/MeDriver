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

package com.mmdev.me.driver.domain.vehicle

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import kotlinx.coroutines.flow.Flow

/**
 * Vehicle repository provides data for [com.mmdev.me.driver.presentation.ui.vehicle]
 */

interface IVehicleRepository {
	
	/**
	 * Add new [Vehicle] to database and (if possible) to server
	 */
	fun addVehicle(user: UserDataInfo?, vehicle: Vehicle): Flow<SimpleResult<Unit>>
	
	/**
	 * Delete vehicle and all associated data
	 */
	suspend fun deleteVehicle(vin: String): SimpleResult<Unit>
	
	/**
	 * Get expenses for only 1 vehicle indicated by [vin]
	 */
	suspend fun getExpensesInfo(vin: String): SimpleResult<Expenses>
	
	/**
	 * Get fuel consumption history for only 1 vehicle indicated by [vin]
	 */
	suspend fun getFuelConsumption(vin: String): SimpleResult<List<ConsumptionBound>>
	
	/**
	 * Get pending replacement related to one [vehicle]
	 */
	suspend fun getPendingReplacements(vehicle: Vehicle): SimpleResult<Map<SparePart, PendingReplacement?>>
	
	/**
	 * Get list of all saved vehicles in database
	 */
	suspend fun getAllSavedVehicles(): SimpleResult<List<Vehicle>>
	
	/**
	 * Get only one saved vehicle from database
	 */
	suspend fun getSavedVehicle(vin: String): Vehicle?
	
	/**
	 * Get vehicle info from internet by [vin]
	 */
	suspend fun getVehicleInfoByVin(vin: String): SimpleResult<Vehicle>
	
}