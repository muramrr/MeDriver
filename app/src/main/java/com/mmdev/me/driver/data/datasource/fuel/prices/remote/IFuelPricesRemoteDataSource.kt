/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 02:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.remote

import com.mmdev.me.driver.data.datasource.fuel.prices.remote.dto.NetworkFuelModelResponse
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType


interface IFuelPricesRemoteDataSource {
	
	suspend fun requestFuelPrices(date: String) : SimpleResult<Map<FuelType, NetworkFuelModelResponse>>

}