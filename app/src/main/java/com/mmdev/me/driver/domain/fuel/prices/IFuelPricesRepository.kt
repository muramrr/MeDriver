/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.11.2020 00:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.prices

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.domain.fuel.prices.data.Region

/**
 * Fuel Prices repository provides data for [com.mmdev.me.driver.presentation.ui.fuel.prices]
 */

interface IFuelPricesRepository {

	suspend fun getFuelStationsWithPrices(
		date: String,
		region: Region
	) : SimpleResult<List<FuelStationWithPrices>>
	
}