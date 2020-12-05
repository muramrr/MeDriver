/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.api

import com.mmdev.me.driver.data.datasource.fuel.prices.api.dto.FuelPricesDtoResponse
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.data.Region

/**
 * Datasource wrapping Retrofit call
 */

interface IFuelPricesApiDataSource {
	
	/**
	 * Get actual FuelPrices for specific FuelType
	 *
	 * @param date defines date for which prices will be retrieve (should be in ISO 8601 date format)
	 * "yyyy-mm-dd"
	 */
	suspend fun requestFuelPrices(
		date: String, region: Region
	): SimpleResult<Map<FuelType, FuelPricesDtoResponse>>

}