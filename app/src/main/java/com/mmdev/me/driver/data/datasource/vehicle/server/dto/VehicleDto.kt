/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 19:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.server.dto

import com.google.firebase.firestore.PropertyName
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.vehicle.data.Regulation

/**
 *
 */

data class VehicleDto (
	@PropertyName("brand") val brand: String = "",
	@PropertyName("model") val model: String = "",
	@PropertyName("year") val year: Int = 0,
	@PropertyName("vin") val vin: String = "",
	@PropertyName("odometerValue") val odometerValue: DistanceBound = DistanceBound(),
	@PropertyName("engineCapacity") val engineCapacity: Double = 0.0,
	@PropertyName("maintenanceRegulations") val maintenanceRegulations: Map<String, Regulation> = mapOf(),
	@PropertyName("lastRefillDate") val lastRefillDate: String = "",
	@PropertyName("dateUpdated") val dateUpdated: Long = 0
)