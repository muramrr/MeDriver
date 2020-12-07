/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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