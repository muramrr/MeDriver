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

import com.mmdev.me.driver.core.utils.helpers.DateHelper.YEAR_DURATION
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 *
 */

data class MaintenanceRegulations(
	val cabinFilter: Regulation = Regulation(
		DistanceBound(kilometers = 15000, miles = null),
		YEAR_DURATION * 2
	),
	val breaksFluid: Regulation = Regulation(
		DistanceBound(kilometers = 40000, miles = null),
		YEAR_DURATION * 2
	),
	val fuelFilter: Regulation = Regulation(
		DistanceBound(kilometers = 40000, miles = null),
		YEAR_DURATION * 2
	),
	val grmKit: Regulation = Regulation(
		DistanceBound(kilometers = 60000, miles = null),
		YEAR_DURATION * 5
	),
	val tires: Regulation = Regulation(
		DistanceBound(kilometers = 80000, miles = null),
		YEAR_DURATION * 10
	)
)