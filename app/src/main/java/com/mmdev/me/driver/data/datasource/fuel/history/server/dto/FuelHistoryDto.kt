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

package com.mmdev.me.driver.data.datasource.fuel.history.server.dto

import com.google.firebase.firestore.PropertyName
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation

/**
 * Used to separate data class dependence between data and domain layers
 * Used only to represent data stored on backend
 *
 * @see com.mmdev.me.driver.data.datasource.fuel.history.server.FuelHistoryServerDataSourceImpl
 */

data class FuelHistoryDto (
	@PropertyName("commentary") val commentary: String = "",
	@PropertyName("date") val date: String = "",
	@PropertyName("dateAdded") val dateAdded: Long = 0,
	@PropertyName("distancePassed") val distancePassed: DistanceBound = DistanceBound(),
	@PropertyName("filledLiters") val filledLiters: Double = 0.0,
	@PropertyName("fuelConsumption") val fuelConsumption: ConsumptionBound = ConsumptionBound(),
	@PropertyName("fuelPrice") val fuelPrice: FuelPrice = FuelPrice(),
	@PropertyName("fuelStation") val fuelStation: FuelStation = FuelStation(),
	@PropertyName("moneySpent") val moneySpent: Double = 0.0,
	@PropertyName("odometerValue") val odometerValue: DistanceBound = DistanceBound(),
	@PropertyName("vehicleVinCode") val vehicleVinCode: String = ""
)