/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 02:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel

import com.mmdev.me.driver.data.datasource.fuel.prices.remote.dto.NetworkFuelModelResponse
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.readJson
import kotlinx.serialization.json.Json

/**
 *
 */

object FuelConstants {
	
	private val responseList = listOf(
		readJson("A100"),
		readJson("A98"),
		readJson("A95PLUS"),
		readJson("A95"),
		readJson("A92"),
		readJson("DT"),
		readJson("GAS")
	)
	
	
	val networkResponse: Map<FuelType, NetworkFuelModelResponse> =
		FuelType.values().zip(
			responseList.map {
				Json {ignoreUnknownKeys = true}
					.decodeFromString(NetworkFuelModelResponse.serializer(), it)
			}
		).toMap()
	
	
	
	
}