/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.09.2020 16:36
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
import com.mmdev.me.driver.domain.fuel.history.model.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound

/**
 * Used in FuelHistoryLocalDataSource
 */

@Entity(tableName = "fuel_history")
data class FuelHistoryEntity(
	val commentary: String,
	@PrimaryKey
	val date: Long,
	val dateAdded: Long,
	@Embedded(prefix = "distance_passed_")
	val distancePassedBound: DistanceBound,
	val filledLiters: Double,
	@Embedded
	val fuelConsumptionBound: ConsumptionBound,
	@Embedded
	val fuelPrice: FuelPriceEntity,
	@Embedded
	val fuelStation: FuelStationEntity,
	@Embedded(prefix = "odometer_history_")
	val odometerValueBound: DistanceBound,
	val vehicleVinCode: String
)