/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.08.2020 17:58
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


/**
 *
 */

data class FuelHistoryRecord (
	val date: Date,
	val distancePassed: Int = 0,
	val filledLiters: Double = 0.0,
	val fuelConsumption: Double = 0.0,
	val fuelPrice: FuelPrice = FuelPrice(),
	val fuelStation: FuelStation = FuelStation(),
	val moneyCosts: Double = (fuelPrice.price * distancePassed).roundTo(2),
	val odometerValue: Int = 0
) {
	val dateText = DateConverter.toText(date)
	val dateMonthText: String = date.getMonthText()
}