/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fetching

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserData
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
	suspend fun updateVehicle(user: UserData?, vehicle: Vehicle): Flow<SimpleResult<Unit>>
	
}