/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 18:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.fuel.model

import com.google.gson.annotations.SerializedName
import com.mmdev.me.driver.domain.fuel.FuelProvider

/**
 * network models which is using by retrofit and remote data source
 */

data class NetworkFuelProvider(
	@SerializedName("value")
	val price: Float = 0f,
	@SerializedName("marka")
	val brand: String = "",
	val slug: String = "") {
	
	fun toUI() = FuelProvider(price.toString(), brand, slug)
}

data class NetworkFuelSummary(
	@SerializedName("minval")
	val minPrice: String = "",
	@SerializedName("maxval")
	val maxPrice: String = "",
	@SerializedName("avgval")
	val avgPrice: String = ""
)



data class NetworkFuelModel(
	@SerializedName("data")
	val networkFuelProviders: List<NetworkFuelProvider> = emptyList(),
	@SerializedName("date")
	val pricesLastUpdatedDate: String = "",
	@SerializedName("total")
	val fuelSummaryResponse: List<NetworkFuelSummary> = emptyList()
)

data class NetworkFuelModelResponse(val result: NetworkFuelModel)




