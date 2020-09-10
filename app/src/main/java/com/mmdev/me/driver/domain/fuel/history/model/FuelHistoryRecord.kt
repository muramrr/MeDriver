/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.09.2020 22:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history.model

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.DateConverter
import com.mmdev.me.driver.core.utils.DateConverter.getMonthText
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.roundTo
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import java.util.*
import kotlin.math.roundToInt


/**
 * Data class primary used at FuelFragmentHistory and DialogFragmentHistoryAdd
 */

data class FuelHistoryRecord (
	val id: Long,
	val commentary: String = "",
	val date: Date = Date(),
	val distancePassedBound: DistanceBound = DistanceBound(),
	val filledLiters: Double = 0.0,
	val fuelConsumptionBound: ConsumptionBound = ConsumptionBound(),
	val fuelPrice: FuelPrice = FuelPrice(),
	val fuelStation: FuelStation = FuelStation(),
	val odometerValueBound: DistanceBound = DistanceBound()
) {
	val distancePassed = when (MedriverApp.metricSystem) {
		MetricSystem.KILOMETERS -> distancePassedBound.kilometers
		MetricSystem.MILES -> distancePassedBound.miles
	}
	
	val fuelConsumption = when (MedriverApp.metricSystem) {
		MetricSystem.KILOMETERS -> fuelConsumptionBound.consumptionPer100KM
		MetricSystem.MILES -> fuelConsumptionBound.consumptionPer100MI
	}
	
	val odometerValue = when (MedriverApp.metricSystem) {
		MetricSystem.KILOMETERS -> odometerValueBound.kilometers
		MetricSystem.MILES -> odometerValueBound.miles
	}
	
	val dateText = DateConverter.toText(date)
	val dateMonthText: String = date.getMonthText()
	val moneyCosts: Double = (fuelPrice.price * filledLiters).roundTo(2)
	val estimateDistance: Int =
		if(fuelConsumption > 0) ((filledLiters / fuelConsumption) * 100).roundToInt()
		else 0
}

