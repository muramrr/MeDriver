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
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Extension functions for domain data class [DistanceBound]
 * Used to free domain classes from application logic and use inside xml DataBinding
 */

fun DistanceBound.getValue(): Int = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> kilometers
	MetricSystem.MILES -> miles
}

fun DistanceBound?.getOdometerFormatted(context: Context): String = when (MedriverApp.metricSystem) {
	MetricSystem.KILOMETERS -> context.getString(R.string.formatter_odometer_km).format(
		this?.kilometers ?: context.getString(R.string.default_OdometerValue)
	)
	MetricSystem.MILES -> context.getString(R.string.formatter_odometer_mi).format(
		this?.miles ?: context.getString(R.string.default_OdometerValue)
	)
}

fun DistanceBound.getDistancePassedFormatted(context: Context): String = when (MedriverApp.metricSystem) {
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