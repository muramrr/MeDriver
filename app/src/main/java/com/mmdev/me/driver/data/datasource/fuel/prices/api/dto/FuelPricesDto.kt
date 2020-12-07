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

package com.mmdev.me.driver.data.datasource.fuel.prices.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Api DTOs used by [com.mmdev.me.driver.data.datasource.fuel.prices.api.retrofit.FuelApi]
 *
 * Represents data from JSON response
 */

@Serializable
data class FuelPriceAndStationDto(
	@SerialName("value")
	val price: Double,
	@SerialName("marka")
	val brand: String,
	val slug: String
)

@Serializable
data class FuelSummaryDto(
	@SerialName("minval")
	val minPrice: String,
	@SerialName("maxval")
	val maxPrice: String,
	@SerialName("avgval")
	val avgPrice: String
)


@Serializable
data class FuelPricesDto(
	@SerialName("data")
	val fuelPriceAndStationDtos: List<FuelPriceAndStationDto> = emptyList(),
	@SerialName("date")
	val pricesLastUpdatedDate: String = "",
	@SerialName("total")
	val fuelSummaryDto: List<FuelSummaryDto> = emptyList()
)

@Serializable
data class FuelPricesDtoResponse(val result: FuelPricesDto)




