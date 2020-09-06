/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity

/**
 * Used in Fuel History Local DataSource
 */

@Entity(tableName = "fuel_history")
data class FuelHistoryEntity(
	@PrimaryKey(autoGenerate = true)
	var historyEntryId: Long,
	val commentary: String,
	val distancePassed: Int,
	val filledLiters: Double = 0.0,
	val fuelConsumption: Double = 0.0,
	@Embedded
	val fuelPrice: FuelPriceEntity,
	@Embedded
	val fuelStation: FuelStationEntity,
	val odometerValue: Int,
	val timestamp: Long
)