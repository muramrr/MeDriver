/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 17:10
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle.data

import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Domain data representation
 * All of DTOs and entities (room) converts to this class (repository mappers responsibility)
 *
 * Used in UI
 * @see com.mmdev.me.driver.presentation.ui.vehicle.VehicleAddBottomSheet
 *
 * @param brand describes vehicle brand (eg: Ford, VW, Audi, Mercedes etc)
 * @param model describes vehicle model (eg: Focus, Tuareg, Q7, E500 etc)
 * @param year represents year vehicle produced
 * @param vin [https://en.wikipedia.org/wiki/Vehicle_identification_number]
 * @param odometerValueBound represents odometer value (in miles and kilometers) at current moment
 * @param engineCapacity describes engine capacity in liters (eg: 1.6, 2.0, 4.0 etc)
 */

data class Vehicle(
	val brand: String,
	val model: String,
	val year: Int,
	val vin: String,
	val odometerValueBound: DistanceBound,
	val engineCapacity: Double
)