/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 00:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.remote.dto

import com.mmdev.me.driver.domain.fuel.history.model.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import java.util.*

/**
 * Dto used in firestore database
 */

data class FuelHistoryDto (
	val commentary: String = "",
	val date: Date = Date(),
	val dateAdded: Date = Date(),
	val distancePassedBound: DistanceBound = DistanceBound(),
	val filledLiters: Double = 0.0,
	val fuelConsumptionBound: ConsumptionBound = ConsumptionBound(),
	val fuelPrice: FuelPrice = FuelPrice(),
	val fuelStation: FuelStation = FuelStation(),
	val odometerValueBound: DistanceBound = DistanceBound(),
	val vehicleVinCode: String
)