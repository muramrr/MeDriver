/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.12.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.remote.dto

import com.google.firebase.firestore.PropertyName
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation

/**
 * Used to separate data class dependence between data and domain layers
 * Used only to represent data stored on backend
 *
 * @see com.mmdev.me.driver.data.datasource.fuel.history.remote.FuelHistoryRemoteDataSourceImpl
 */

data class FuelHistoryDto (
	@PropertyName("commentary") val commentary: String = "",
	@PropertyName("date") val date: String = "",
	@PropertyName("dateAdded") val dateAdded: Long = 0,
	@PropertyName("distancePassedBound") val distancePassed: DistanceBound = DistanceBound(),
	@PropertyName("filledLiters") val filledLiters: Double = 0.0,
	@PropertyName("fuelConsumptionBound") val fuelConsumption: ConsumptionBound = ConsumptionBound(),
	@PropertyName("fuelPrice") val fuelPrice: FuelPrice = FuelPrice(),
	@PropertyName("fuelStation") val fuelStation: FuelStation = FuelStation(),
	@PropertyName("moneySpent") val moneySpent: Double = 0.0,
	@PropertyName("odometerValueBound") val odometerValue: DistanceBound = DistanceBound(),
	@PropertyName("vehicleVinCode") val vehicleVinCode: String = ""
)