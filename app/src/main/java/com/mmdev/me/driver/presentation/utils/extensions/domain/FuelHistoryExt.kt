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

package com.mmdev.me.driver.presentation.utils.extensions.domain

import android.content.Context
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory

/**
 * Contains logic to calculate secondary values based on given
 */

fun ConsumptionBound.getValue(): Double = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> consumptionPer100KM.roundTo(1)
	MetricSystem.MILES -> consumptionPer100MI.roundTo(1)
}

fun ConsumptionBound.getValueFormatted(context: Context): String = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> context.getString(R.string.item_fuel_history_entry_consumption_km).format(consumptionPer100KM.roundTo(1))
	MetricSystem.MILES -> context.getString(R.string.item_fuel_history_entry_consumption_mi).format(consumptionPer100MI.roundTo(1))
}

fun FuelHistory.getMoneySpentFormatted(context: Context): String =
	context.getString(R.string.price_formatter_left).format(moneySpent.roundTo(2))


//	val estimateDistance: Int =
//		if(fuelConsumption > 0) ((filledLiters / fuelConsumption) * 100).roundToInt()
//		else 0