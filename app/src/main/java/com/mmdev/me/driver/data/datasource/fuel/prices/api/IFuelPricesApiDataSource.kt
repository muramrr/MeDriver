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