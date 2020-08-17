/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:35
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
	val distancePassed: Double = 0.0,
	val odometerValue: Double = 0.0,
	val fuelStation: FuelStation = FuelStation(),
	val fuelPrice: FuelPrice = FuelPrice(),
	val moneyCosts: Double = (fuelPrice.price * distancePassed).roundTo(2),
	val fuelConsumption: Double = 0.0,
	val date: Date
) {
	val dateText = DateConverter.toText(date)
	val dateMonthText: String = date.getMonthText()
}