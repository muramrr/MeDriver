/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.10.2020 18:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.extensions.domain

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