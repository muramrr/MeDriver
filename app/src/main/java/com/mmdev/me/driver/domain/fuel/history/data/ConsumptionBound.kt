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

package com.mmdev.me.driver.domain.fuel.history.data

import com.mmdev.me.driver.core.utils.extensions.roundTo


/**
 * Wrapping fuel consumption value
 */

data class ConsumptionBound (
	val consumptionPer100KM: Double = 0.0,
	val consumptionPer100MI: Double = 0.0
) {
	
	constructor(consumptionKM: Double, consumptionMI: Double? = null) :
			this(consumptionKM, (consumptionKM * 1.609).roundTo(2))
	
	constructor(consumptionKM: Double? = null, consumptionMI: Double) :
			this((consumptionMI / 1.609).roundTo(2), consumptionMI)
}