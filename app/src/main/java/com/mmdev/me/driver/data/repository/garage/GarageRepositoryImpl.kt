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

package com.mmdev.me.driver.data.repository.garage

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.garage.IGarageLocalDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.garage.IGarageRepository
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle

/**
 * [IGarageRepository] implementation
 */

class GarageRepositoryImpl(
	private val localDataSource: IGarageLocalDataSource,
	private val mappers: VehicleMappersFacade
): BaseRepository(), IGarageRepository {
	
	override suspend fun getGarage(): List<Pair<Vehicle, Expenses>> =
		localDataSource.getMyGarage().map {
			Pair(mappers.entityToDomain(it.vehicle), it.expenses)
		}
		
	
	override suspend fun getExpensesByTimeRange(
		monthsRange: List<Pair<Long, Long>>
	): List<Expenses> = monthsRange.map { range ->
		localDataSource.getExpensesBetweenTimeRange(range.first, range.second)
	}
	
}