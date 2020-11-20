/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.11.2020 17:39
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.remote.dto

import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.vehicle.data.Regulation

/**
 *
 */

data class VehicleDto (
	val brand: String = "",
	val model: String = "",
	val year: Int = 0,
	val vin: String = "",
	val odometerValueBound: DistanceBound = DistanceBound(),
	val engineCapacity: Double = 0.0,
	val maintenanceRegulations: Map<String, Regulation> = mapOf()
)