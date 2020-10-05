/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.core.utils.roundTo
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import kotlinx.datetime.LocalDate
import java.util.*

/**
 * Contains logic to calculate secondary values based on given
 */

fun ConsumptionBound.getValue(): Double = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> consumptionPer100KM
	MetricSystem.MILES -> consumptionPer100MI
}

fun FuelHistory.moneySpent(): Double = (fuelPrice.price * filledLiters).roundTo(2)

fun DistanceBound.getValue(): Int = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> kilometers
	MetricSystem.MILES -> miles
}

fun Int.dateMonthText(): String = DateHelper.getMonthText(this, Locale.getDefault())

// date in format "01.01.1970"
fun LocalDate.humanDate(): String =
	"$dayOfMonth." + (if (monthNumber < 10) "0$monthNumber" else "$monthNumber") + ".$year"

//	val estimateDistance: Int =
//		if(fuelConsumption > 0) ((filledLiters / fuelConsumption) * 100).roundToInt()
//		else 0