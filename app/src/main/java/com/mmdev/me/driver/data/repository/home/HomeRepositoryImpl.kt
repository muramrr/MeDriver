/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.11.2020 20:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.home

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.home.IHomeLocalDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.home.IHomeRepository
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle

/**
 * [IHomeRepository]
 */

class HomeRepositoryImpl(
	private val localDataSource: IHomeLocalDataSource,
	private val mappers: VehicleMappersFacade
): BaseRepository(), IHomeRepository {
	
	override suspend fun getGarage(): SimpleResult<List<Pair<Vehicle, Expenses>>> =
		localDataSource.getMyGarage().fold(
			success = { result ->
				ResultState.success(
					result.map {
						Pair(mappers.entityToDomain(it.vehicle), it.expenses)
					}
				)
			},
			failure = {
				ResultState.failure(it)
			}
		)
	
	override suspend fun getExpensesByTimeRange(
		monthsRange: List<Pair<Long, Long>>
	): List<Expenses> = monthsRange.map { range ->
		localDataSource.getExpensesBetweenTimeRange(range.first, range.second)
	}
	
}