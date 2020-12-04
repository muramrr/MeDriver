/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.home

import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.datasource.home.dao.HomeDao
import com.mmdev.me.driver.data.datasource.home.entity.VehicleWithExpenses
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IHomeLocalDataSource] implementation
 */

class HomeLocalDataSourceImpl(private val dao: HomeDao): BaseDataSource(), IHomeLocalDataSource {
	
	override suspend fun getMyGarage(): SimpleResult<List<VehicleWithExpenses>> = safeCall(TAG) {
		dao.getAllVehiclesWithExpenses()
	}
	
	override suspend fun getExpensesBetweenTimeRange(
		start: Long, end: Long
	) = dao.getExpensesBetweenTime(start, end)
	
}