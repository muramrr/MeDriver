/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 20:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import kotlinx.coroutines.flow.Flow

/**
 * Vehicle repository provides data for [com.mmdev.me.driver.presentation.ui.vehicle]
 */

interface IVehicleRepository {
	
	suspend fun getAllVehicles(user: UserModel?): Flow<SimpleResult<List<Vehicle>>>
	
	//called only on app startup
	suspend fun getVehicle(vin: String): Vehicle?
	
	suspend fun addVehicle(user: UserModel?, vehicle: Vehicle): Flow<SimpleResult<Unit>>
}