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
 * Used to modify current global state of application
 * Used inside only [com.mmdev.me.driver.presentation.ui.SharedViewModel]
 */

interface IFetchingRepository {
	
	//retrieve vehicle info on app start up
	suspend fun getSavedVehicle(vin: String): Vehicle?
	
	//after minor changes -> save all to database and remote (if premium and possible)
	suspend fun updateVehicle(user: UserDataInfo?, vehicle: Vehicle): Flow<SimpleResult<Unit>>
	
	suspend fun listenForUpdates(email: String)
	
}