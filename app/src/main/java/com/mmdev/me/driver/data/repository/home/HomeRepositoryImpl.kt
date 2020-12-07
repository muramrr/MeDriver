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