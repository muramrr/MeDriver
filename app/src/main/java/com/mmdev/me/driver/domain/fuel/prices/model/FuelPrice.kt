/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 02:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.prices.model

import com.mmdev.me.driver.domain.fuel.FuelType

/**
 *
 */

data class FuelPrice(val price: Double = 0.0, val type: FuelType = FuelType.A95) {
	
	constructor(price: Double, type: Int): this(
		price = price,
		type = FuelType.values().find { it.code == type } ?: FuelType.A95
	)
	
}