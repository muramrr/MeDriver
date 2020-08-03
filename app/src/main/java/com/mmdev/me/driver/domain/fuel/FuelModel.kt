/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 18:55
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel

import com.google.gson.annotations.SerializedName
import com.mmdev.me.driver.presentation.ui.fuel.FuelProvidersMap

/**
 *
 */

data class FuelProvider(
	@SerializedName("value")
	val price: Float = 0f,
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
	A100(1), A95PLUS(2), A95(3), A92(4), DT(5), GAS(6)
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

data class FuelProviderUI (
	val price: String = "",
	val brandTitle: String,
	val slug: String,
	val brandIcon: Int =
		if (FuelProvidersMap.fuelProvidersMap.containsKey(slug))
			FuelProvidersMap.fuelProvidersMap.getValue(slug)
		else 0
)

fun FuelProvider.toUI() = FuelProviderUI(price.toString(), brand, slug)

