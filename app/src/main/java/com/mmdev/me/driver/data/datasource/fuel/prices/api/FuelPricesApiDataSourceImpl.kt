/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 21:06
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.api


import com.mmdev.me.driver.core.utils.extensions.toMap
import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.api.dto.FuelPricesDtoResponse
import com.mmdev.me.driver.data.datasource.fuel.prices.api.retrofit.FuelApi
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.data.Region
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * [IFuelPricesApiDataSource] implementation
 */

class FuelPricesApiDataSourceImpl(
	private val fuelApi: FuelApi
) : IFuelPricesApiDataSource, BaseDataSource() {
	
	override suspend fun requestFuelPrices(date: String, region: Region): SimpleResult<Map<FuelType,
			FuelPricesDtoResponse>> =
		safeCall(TAG) { getPricesPerType(date, FuelType.values().asIterable(), region.id) }
	
	//get all prices for every fuel type
	//response contains list of FuelStations and FuelPrices for specified fuel type
	//single response per each fuel type, so flow concurrency comes in here
	private suspend fun getPricesPerType(
		date:String,
		fuelTypes: Iterable<FuelType>,
		region: Int
	): Map<FuelType, FuelPricesDtoResponse> =
		fuelTypes.asFlow().flatMapMerge(concurrency = fuelTypes.count()) { fuelType ->
			flow { emit(fuelType to fuelApi.getFuelInfoFromApi(date, fuelType.code, region)) }
		}.toMap()
}