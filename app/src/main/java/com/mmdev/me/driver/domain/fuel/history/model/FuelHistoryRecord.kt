/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 00:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history.model

import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import java.util.*


/**
 * Data class primary used in FuelFragmentHistory and DialogFragmentHistoryAdd
 */

data class FuelHistoryRecord (
	val commentary: String = "",
	val date: Date = Date(),
	val distancePassedBound: DistanceBound = DistanceBound(),
	val filledLiters: Double = 0.0,
	val fuelConsumptionBound: ConsumptionBound = ConsumptionBound(),
	val fuelPrice: FuelPrice = FuelPrice(),
	val fuelStation: FuelStation = FuelStation(),
	val odometerValueBound: DistanceBound = DistanceBound(),
	val vehicleVinCode: String
)

