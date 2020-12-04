/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel


/**
 * Used to define map: which [com.mmdev.me.driver.data.datasource.fuel.prices.api.dto.FuelPricesDto]
 * corresponds to a given [FuelType]
 */

enum class FuelType (val code: Int) {
	GAS(6),
	DT(5),
	A92(4),
	A95(3),
	A95PLUS(2),
	A98(1),
	A100(9)
	;
	
	companion object {
		fun getType(code: Int) = when (code) {
			1 -> A98
			2 -> A95PLUS
			3 -> A95
			4 -> A92
			5 -> DT
			6 -> GAS
			9 -> A100
			else -> throw NoSuchElementException("Fuel type doesn't exists")
		}
	}
}