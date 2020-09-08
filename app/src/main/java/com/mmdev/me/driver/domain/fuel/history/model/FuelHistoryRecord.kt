/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.09.2020 02:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history.model

import com.mmdev.me.driver.core.utils.DateConverter
import com.mmdev.me.driver.core.utils.DateConverter.getMonthText
import com.mmdev.me.driver.core.utils.roundTo
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import java.util.*
import kotlin.math.roundToInt


/**
 *
 */

data class FuelHistoryRecord (
	val id: Long,
	val commentary: String = "",
	val date: Date = Date(),
	val distancePassed: Int = 0,
	val filledLiters: Double = 0.0,
	val fuelConsumption: Double = 0.0,
	val fuelPrice: FuelPrice = FuelPrice(),
	val fuelStation: FuelStation = FuelStation(),
	val odometerValue: Int = 0
) {
	data class DistancePassed(
		val kilometers: Int = 0,
		val miles: Int = 0
	) {
		constructor(kilometers: Int, miles: Int? = null) : this(kilometers, (kilometers / 1.609).roundToInt())
		constructor(kilometers: Int? = null, miles: Int) : this((miles * 1.609).roundToInt(), miles)
	}
	
	val dateText = DateConverter.toText(date)
	val dateMonthText: String = date.getMonthText()
	val moneyCosts: Double = (fuelPrice.price * filledLiters).roundTo(2)
	val estimateDistance: Int =
		if(fuelConsumption > 0) ((filledLiters / fuelConsumption) * 100).roundToInt()
		else 0
}

