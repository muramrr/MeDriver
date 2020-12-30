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

package com.mmdev.me.driver.domain.fetching

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import kotlinx.coroutines.flow.Flow

/**
 * Used inside only [com.mmdev.me.driver.presentation.ui.SharedViewModel]
 */

interface IFetchingRepository {
	
	/**
	 * Retrieve vehicle info from room database
	 * Called on app startup
	 */
	suspend fun getSavedVehicle(vin: String): Vehicle?
	
	/**
	 * After minor changes -> save actual info to database and server (if pro and possible)
	 */
	fun updateVehicle(user: UserDataInfo?, vehicle: Vehicle): Flow<SimpleResult<Unit>>
	
	/**
	 * Listen for realtime server updates
	 * No receiver is needed, all data will be downloaded and fetched automatically
	 */
	suspend fun listenForUpdates(email: String)
	
}