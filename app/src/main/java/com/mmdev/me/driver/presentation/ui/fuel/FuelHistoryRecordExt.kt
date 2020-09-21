/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 16:08
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
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord

/**
 * Contains logic to calculate secondary values based on given
 */

fun FuelHistoryRecord.distancePassed(): Int = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> distancePassedBound.kilometers
	MetricSystem.MILES -> distancePassedBound.miles
}

fun FuelHistoryRecord.fuelConsumption(): Double = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> fuelConsumptionBound.consumptionPer100KM
	MetricSystem.MILES -> fuelConsumptionBound.consumptionPer100MI
}

fun FuelHistoryRecord.moneyCosts(): Double = (fuelPrice.price * filledLiters).roundTo(2)

fun FuelHistoryRecord.odometerValue(): Int = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> vehicle.odometerValueBound.kilometers
	MetricSystem.MILES -> vehicle.odometerValueBound.miles
}

fun FuelHistoryRecord.dateMonthText(): String = date.getMonthText()


fun FuelHistoryRecord.date(): String = toText(date)

//	val estimateDistance: Int =
//		if(fuelConsumption > 0) ((filledLiters / fuelConsumption) * 100).roundToInt()
//		else 0