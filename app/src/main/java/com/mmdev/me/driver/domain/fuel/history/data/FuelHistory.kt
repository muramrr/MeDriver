/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.12.2020 20:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history.data


import com.mmdev.me.driver.domain.fuel.FuelType.A95PLUS
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import kotlinx.datetime.LocalDateTime

/**
 *
 */

data class FuelHistory (
	val date: LocalDateTime,
	val dateAdded: Long,
	val distancePassedBound: DistanceBound = DistanceBound(),
	val filledLiters: Double = 0.0,
	val fuelConsumptionBound: ConsumptionBound = ConsumptionBound(),
	val fuelPrice: FuelPrice = FuelPrice(),
	val fuelStation: FuelStation = FuelStation(),
	val moneySpent: Double = (fuelPrice.price * filledLiters),
	val odometerValueBound: DistanceBound = DistanceBound(),
	val vehicleVinCode: String,
	val commentary: String = ""
) {
	fun stationAndType(): String =
		"${fuelStation.brandTitle}, " + if (fuelPrice.type == A95PLUS) "A95+" else fuelPrice.type.name
}

