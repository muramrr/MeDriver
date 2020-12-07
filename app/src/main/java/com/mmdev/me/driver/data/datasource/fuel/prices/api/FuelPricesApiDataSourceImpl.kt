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