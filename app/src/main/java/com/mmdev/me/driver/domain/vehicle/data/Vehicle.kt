/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.11.2020 15:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle.data

import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.ENGINE_OIL_FILTER
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.FILTER_AIR
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart

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
	val engineCapacity: Double,
	val maintenanceRegulations: Map<SparePart, Regulation?> = mapOf(
		FILTER_AIR to Regulation(
			DistanceBound(kilometers = 10000, miles = null), DateHelper.YEAR_DURATION
		),
		ENGINE_OIL_FILTER to Regulation(
			DistanceBound(kilometers = 12000, miles = null), DateHelper.YEAR_DURATION
		)
	)
)