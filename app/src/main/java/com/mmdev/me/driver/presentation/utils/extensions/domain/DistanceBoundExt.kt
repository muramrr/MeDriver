/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.10.2020 19:53
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
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Extension functions for domain data class [DistanceBound]
 * Used to free domain classes from application logic and use inside xml DataBinding
 */

fun DistanceBound.getOdometerValue(): Int = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> kilometers
	MetricSystem.MILES -> miles
}

fun DistanceBound.getOdometerFormatted(context: Context): String = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> context.getString(R.string.formatter_odometer_km).format(kilometers)
	MetricSystem.MILES -> context.getString(R.string.formatter_odometer_mi).format(miles)
}

fun DistanceBound.getDistancePassed(context: Context): String = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> context.getString(R.string.item_fuel_history_entry_distance_passed_km).format(kilometers)
	MetricSystem.MILES -> context.getString(R.string.item_fuel_history_entry_distance_passed_mi).format(miles)
}


/**
 * Build [DistanceBound] data class for specified [value] according to what metric system app
 * is using.
 * Metric system can be changed at SettingsFragment
 */
fun buildDistanceBound(value: Int): DistanceBound =
	when (MedriverApp.metricSystem) {
		MetricSystem.KILOMETERS -> DistanceBound(
			kilometers = value,
			miles = null
		)
		MetricSystem.MILES -> DistanceBound(
			kilometers = null,
			miles = value
		)
	}