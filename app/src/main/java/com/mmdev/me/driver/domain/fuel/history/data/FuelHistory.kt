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

