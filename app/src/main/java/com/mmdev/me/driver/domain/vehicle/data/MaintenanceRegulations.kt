/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.11.2020 18:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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