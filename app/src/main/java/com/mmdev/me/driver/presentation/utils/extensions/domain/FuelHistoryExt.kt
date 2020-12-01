/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.12.2020 20:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.extensions.domain

import android.content.Context
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory

/**
 * Contains logic to calculate secondary values based on given
 */

fun FuelHistory.getConsumptionValue(): Double = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> fuelConsumptionBound.consumptionPer100KM.roundTo(1)
	MetricSystem.MILES -> fuelConsumptionBound.consumptionPer100MI.roundTo(1)
}

fun FuelHistory.getConsumptionFormatted(context: Context): String = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> context.getString(R.string.item_fuel_history_entry_consumption_km).format(fuelConsumptionBound.consumptionPer100KM.roundTo(1))
	MetricSystem.MILES -> context.getString(R.string.item_fuel_history_entry_consumption_mi).format(fuelConsumptionBound.consumptionPer100MI.roundTo(1))
}

fun FuelHistory.getMoneySpentFormatted(context: Context): String =
	context.getString(R.string.price_formatter_left).format(moneySpent.roundTo(2))


//	val estimateDistance: Int =
//		if(fuelConsumption > 0) ((filledLiters / fuelConsumption) * 100).roundToInt()
//		else 0