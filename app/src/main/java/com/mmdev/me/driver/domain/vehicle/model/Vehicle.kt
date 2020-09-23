/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.09.2020 02:22
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle.model

import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound

/**
 *
 */

data class Vehicle(
	val brand: String,
	val model: String,
	val year: Int,
	val vin: String,
	val odometerValueBound: DistanceBound,
	val engineCapacity: Double
)