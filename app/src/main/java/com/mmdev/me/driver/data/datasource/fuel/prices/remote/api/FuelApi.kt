/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:55
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.remote.api

import com.mmdev.me.driver.data.datasource.fuel.prices.remote.model.NetworkFuelModelResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * region (obl) == 3 is Kyiv
 */

interface FuelApi {
	
	// ?obl=3date=2020-08-02&type=1
	private companion object {
		private const val PRICE_URL = "https://carta.ua/ajax/fuelprice/price?obl=3"
	}
	
	@GET(PRICE_URL)
	suspend fun getFuelInfoFromApi(
		@Query("date") date: String,
		@Query("type") fuelType: Int,
		//@Query("obl") region: Int = 3
	): NetworkFuelModelResponse
	
}