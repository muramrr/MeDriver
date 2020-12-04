/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 21:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation

/**
 * Used to separate data class dependence between data and domain layers
 * Used only to represent data stored in local database
 *
 * @see com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
 */

@Entity(tableName = MeDriverRoomDatabase.FUEL_HISTORY_TABLE)
data class FuelHistoryEntity(
	val commentary: String,
	val date: String,
	@PrimaryKey
	val dateAdded: Long,
	@Embedded(prefix = "fuel_history_distance_passed_")
	val distancePassedBound: DistanceBound,
	val filledLiters: Double,
	@Embedded
	val fuelConsumptionBound: ConsumptionBound,
	@Embedded(prefix = "fuel_history_")
	val fuelPrice: FuelPriceEmbedded,
	@Embedded(prefix = "fuel_history_")
	val fuelStation: FuelStation,
	val moneySpent: Double,
	@Embedded(prefix = "fuel_history_odometer_")
	val odometerValueBound: DistanceBound,
	val vehicleVinCode: String
)