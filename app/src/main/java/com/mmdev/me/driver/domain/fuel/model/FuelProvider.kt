/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 21:10
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.model

import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.presentation.ui.fuel.FuelProviderConstants

/**
 * Domain model
 */

data class FuelProvider (
	val brandTitle: String = "",
	val slug: String = "",
	val prices: List<FuelPrice> = emptyList(),
	val updatedDate: String = ""
) {
	
	val brandIcon: Int =
		if (FuelProviderConstants.fuelProvidersMap.containsKey(slug))
			FuelProviderConstants.fuelProvidersMap.getValue(slug)
	else 0
	
	data class FuelPrice(val type: FuelType, val price: String) {
		
		constructor(type: Int, price: Float): this(
			type = FuelType.values().find { it.code == type } ?: FuelType.A95,
			price = price.toString()
		)
		
		constructor(type: FuelType, price: Float): this(
			type = type,
			price = price.toString()
		)
		
	}
}
