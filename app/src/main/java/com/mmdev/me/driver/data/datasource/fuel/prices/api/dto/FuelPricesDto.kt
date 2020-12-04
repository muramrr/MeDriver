/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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




