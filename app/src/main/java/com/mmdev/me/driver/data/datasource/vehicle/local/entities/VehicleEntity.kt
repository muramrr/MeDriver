/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.10.2020 16:28
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound

/**
 *
 */
@Entity(tableName = "vehicles")
data class VehicleEntity (
	val brand: String,
	val model: String,
	val year: Int,
	@PrimaryKey
	val vin: String,
	@Embedded(prefix = "odometer_current_")
	val odometerValueBound: DistanceBound,
	val engineCapacity: Double
)