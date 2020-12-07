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

package com.mmdev.me.driver.data.datasource.fuel.prices.api.retrofit

import com.mmdev.me.driver.data.datasource.fuel.prices.api.dto.FuelPricesDtoResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit call to retrieve fuel prices for specific region, fuel type and date
 */

interface FuelApi {
	
	// ?obl=3date=2020-08-02&type=1
	private companion object {
		private const val PRICE_URL = "https://carta.ua/ajax/fuelprice/price?"
	}
	
	@GET(PRICE_URL)
	suspend fun getFuelInfoFromApi(
		@Query("date") date: String,
		@Query("type") fuelType: Int,
		@Query("obl") region: Int
	): FuelPricesDtoResponse
	
}