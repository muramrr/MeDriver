/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.09.2020 21:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.DateConverter.getMonthText
import com.mmdev.me.driver.core.utils.DateConverter.toText
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.roundTo
import com.mmdev.me.driver.domain.fuel.history.model.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord

/**
 * Contains logic to calculate secondary values based on given
 */

fun ConsumptionBound.getValue(): Double = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> consumptionPer100KM
	MetricSystem.MILES -> consumptionPer100MI
}

fun FuelHistoryRecord.moneySpent(): Double = (fuelPrice.price * filledLiters).roundTo(2)

fun DistanceBound.getValue(): Int = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> kilometers
	MetricSystem.MILES -> miles
}

fun FuelHistoryRecord.dateMonthText(): String = date.getMonthText()


fun FuelHistoryRecord.date(): String = toText(date)

//	val estimateDistance: Int =
//		if(fuelConsumption > 0) ((filledLiters / fuelConsumption) * 100).roundToInt()
//		else 0