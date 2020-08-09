/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 16:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.fuel

import com.mmdev.me.driver.data.datasource.remote.fuel.model.NetworkFuelModelResponse
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 *
 */

interface IFuelRemoteDataSource {
	
	suspend fun getFuelInfo(
		date: String,
		fuelType: Int,
		region: Int = 3
	) : SimpleResult<Map<FuelType, NetworkFuelModelResponse>>

}