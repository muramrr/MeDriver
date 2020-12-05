/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.11.2020 20:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.home

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle

/**
 *
 */

interface IHomeRepository {
	
	suspend fun getGarage(): SimpleResult<List<Pair<Vehicle, Expenses>>>
	
	suspend fun getExpensesByTimeRange(
		monthsRange: List<Pair<Long, Long>>
	): List<Expenses>
	
}