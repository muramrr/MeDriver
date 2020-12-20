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

package com.mmdev.me.driver.domain.vehicle.data

import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.*
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
	val lastRefillDate: String = "",
	val lastUpdatedDate: Long,
	val maintenanceRegulations: Map<SparePart, Regulation> = mapOf(
		INSURANCE to Regulation(DistanceBound(), DateHelper.YEAR_DURATION),
		FILTER_AIR to Regulation(
			DistanceBound(kilometers = 12000, miles = null), DateHelper.YEAR_DURATION
		),
		ENGINE_OIL_FILTER to Regulation(
			DistanceBound(kilometers = 12000, miles = null), DateHelper.YEAR_DURATION
		),
		FILTER_FUEL to Regulation(
			DistanceBound(kilometers = 30000, miles = null), DateHelper.YEAR_DURATION *2
		),
		FILTER_CABIN to Regulation(
			DistanceBound(kilometers = 15000, miles = null), DateHelper.YEAR_DURATION *2
		),
		BRAKES_FLUID to Regulation(
			DistanceBound(kilometers = 60000, miles = null), DateHelper.YEAR_DURATION *2
		),
		GRM_KIT to Regulation(
			DistanceBound(kilometers = 80000, miles = null), DateHelper.YEAR_DURATION *5
		),
		PLUGS to Regulation(
			DistanceBound(kilometers = 90000, miles = null), DateHelper.YEAR_DURATION *5
		),
		TRANSMISSION_OIL_FILTER to Regulation(
			DistanceBound(kilometers = 60000, miles = null), DateHelper.YEAR_DURATION *7
		)
	)
) {
	fun getVehicleUiName() = "$brand $model ($year), $engineCapacity"
}