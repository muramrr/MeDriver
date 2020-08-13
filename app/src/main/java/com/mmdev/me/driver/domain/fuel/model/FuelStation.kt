/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.08.20 18:44
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

data class FuelStation (
	val brandTitle: String = "",
	val slug: String = "",
	val prices: MutableSet<FuelPrice> = mutableSetOf(),
	val updatedDate: String = ""
) {
	
	val brandIcon: Int =
		if (FuelProviderConstants.fuelProvidersIconMap.containsKey(slug))
			FuelProviderConstants.fuelProvidersIconMap.getValue(slug)
	else 0
	
	data class FuelPrice(val type: FuelType, val price: String = "--.--") {
		
		constructor(type: Int, price: Double): this(
			type = FuelType.values().find { it.code == type } ?: FuelType.A95,
			price = price.toString()
		)
		
		constructor(type: FuelType, price: Double): this(
			type = type,
			price = price.toString()
		)
		
	}
}
