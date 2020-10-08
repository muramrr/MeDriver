/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:31
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