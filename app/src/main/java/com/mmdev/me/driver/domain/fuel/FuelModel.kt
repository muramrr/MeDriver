/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 16:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel

import com.google.gson.annotations.SerializedName

/**
 *
 */

data class FuelProvider(
	@SerializedName("value")
	val price: Double,
	@SerializedName("marka")
	val brand: String,
	val slug: String
)


data class FuelSummary(
	@SerializedName("minval")
	val minPrice: String,
	@SerializedName("maxval")
	val maxPrice: String,
	@SerializedName("avgval")
	val avgPrice: String
)

enum class FuelType (val code: Int) {
	AI100(1), AI95PLUS(2), AI95(3), AI92(4), DIESEL(5), GAS(6)
}


data class FuelModelResponse(val result: FuelModel)

data class FuelModel (
	@SerializedName("data")
	val fuelProviders: List<FuelProvider>,
	@SerializedName("date")
	val pricesLastUpdatedDate: String,
	@SerializedName("total")
	val fuelSummaryResponse: List<FuelSummary>
)