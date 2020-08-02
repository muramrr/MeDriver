/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 16:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.fuel

import com.mmdev.me.driver.data.core.ResponseState
import com.mmdev.me.driver.domain.fuel.FuelModelResponse
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 *
 */

interface IFuelDataSourceRemote {
	
	suspend fun getFuelInfo(date: String, fuelType: Int, region: Int = 3) : ResponseState<Map<FuelType, FuelModelResponse>>

}