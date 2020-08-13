/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.08.20 17:06
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.fuel.model

import com.google.gson.annotations.SerializedName

/**
 * network models which is using by retrofit and remote data source
 */

data class NetworkFuelStation(
	@SerializedName("value")
	val price: Double,
	@SerializedName("marka")
	val brand: String,
	val slug: String
)

data class NetworkFuelSummary(
	@SerializedName("minval")
	val minPrice: String,
	@SerializedName("maxval")
	val maxPrice: String,
	@SerializedName("avgval")
	val avgPrice: String
)



data class NetworkFuelModel(
	@SerializedName("data")
	val networkFuelStations: List<NetworkFuelStation>,
	@SerializedName("date")
	val pricesLastUpdatedDate: String,
	@SerializedName("total")
	val fuelSummaryResponse: List<NetworkFuelSummary>
)

data class NetworkFuelModelResponse(val result: NetworkFuelModel)




