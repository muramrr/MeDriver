/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 18:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel

import com.mmdev.me.driver.presentation.ui.fuel.FuelProvidersMap

/**
 * ui models
 */

data class FuelProvider (
	val price: String,
	val brandTitle: String,
	val slug: String,
	val prices: List<FuelPrice>,
	val brandIcon: Int =
		if (FuelProvidersMap.fuelProvidersMap.containsKey(slug))
			FuelProvidersMap.fuelProvidersMap.getValue(slug)
		else 0
) {

	data class FuelPrice(val type: FuelType, val price: Float)

}

data class FuelSummary(
	val type: FuelType,
	val minPrice: String,
	val maxPrice: String,
	val avgPrice: String,
	val updatedDate: String
)