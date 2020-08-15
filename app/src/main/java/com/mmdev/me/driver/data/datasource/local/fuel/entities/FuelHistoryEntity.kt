/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.08.2020 22:26
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.core.utils.roundTo

/**
 *
 */

@Entity(tableName = "fuel_history")
data class FuelHistoryEntity(
	val distancePassed: Double,
	val odometerValue: Double,
	@Embedded
	val fuelStation: FuelStationEntity,
	@Embedded
	val fuelPrice: FuelPriceEntity,
	val moneyCosts: Double = (fuelPrice.price * distancePassed).roundTo(2),
	val fuelConsumption: Double = 0.0,
	val date: String
) {
	@PrimaryKey(autoGenerate = true)
	var historyEntryId: Int = 1
}