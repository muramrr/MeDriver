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