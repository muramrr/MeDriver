/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 17:10
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
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Used to separate data class dependence between data and domain layers
 * Used only to represent data stored in local database
 *
 * @see com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
 */

@Entity(tableName = MeDriverRoomDatabase.FUEL_HISTORY_TABLE)
data class FuelHistoryEntity(
	val commentary: String,
	val date: Long,
	@PrimaryKey
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
	val moneySpent: Double,
	@Embedded(prefix = "odometer_history_")
	val odometerValueBound: DistanceBound,
	val vehicleVinCode: String
)