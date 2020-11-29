/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.11.2020 17:47
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
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound

/**
 * Contains logic to calculate secondary values based on given
 */

fun ConsumptionBound.getValue(): Double = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> consumptionPer100KM.roundTo(1)
	MetricSystem.MILES -> consumptionPer100MI.roundTo(1)
}

fun ConsumptionBound.getTextValue(context: Context): String = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> context.getString(R.string.item_fuel_history_entry_consumption_km).format(consumptionPer100KM.roundTo(1))
	MetricSystem.MILES -> context.getString(R.string.item_fuel_history_entry_consumption_mi).format(consumptionPer100MI.roundTo(1))
}


//	val estimateDistance: Int =
//		if(fuelConsumption > 0) ((filledLiters / fuelConsumption) * 100).roundToInt()
//		else 0