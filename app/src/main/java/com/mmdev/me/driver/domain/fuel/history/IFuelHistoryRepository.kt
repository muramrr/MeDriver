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

package com.mmdev.me.driver.domain.fuel.history

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.user.UserDataInfo
import kotlinx.coroutines.flow.Flow

/**
 * Fuel History repository provides data for [com.mmdev.me.driver.presentation.ui.fuel.history]
 */

interface IFuelHistoryRepository {
	
	suspend fun addFuelHistoryRecord(user: UserDataInfo?, history: FuelHistory): Flow<SimpleResult<Unit>>
	
	suspend fun importFuelHistory(user: UserDataInfo?, history: List<FuelHistory>): Flow<SimpleResult<Unit>>
	
	suspend fun getInitFuelHistory(vin: String): SimpleResult<List<FuelHistory>>
	suspend fun getMoreFuelHistory(vin: String): SimpleResult<List<FuelHistory>>
	suspend fun getPreviousFuelHistory(vin: String): SimpleResult<List<FuelHistory>>
	
	suspend fun loadFirstFuelHistoryEntry(vin: String): SimpleResult<FuelHistory?>
	
	
	suspend fun removeFuelHistoryRecord(user: UserDataInfo?, history: FuelHistory): Flow<SimpleResult<Unit>>
	
}