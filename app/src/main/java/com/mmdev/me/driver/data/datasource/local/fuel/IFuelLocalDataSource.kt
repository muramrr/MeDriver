/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.08.2020 19:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel

import com.mmdev.me.driver.data.datasource.local.fuel.entities.*
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * Fuel DataSource which works with local database
 */

interface IFuelLocalDataSource {
	
	suspend fun getFuelStationsAndPrices(): SimpleResult<List<FuelStationAndPrices>>
	suspend fun addFuelStation(fuelStationEntity: FuelStationEntity)
	suspend fun addFuelPrice(fuelPrice: FuelPriceEntity)
	suspend fun deleteAllFuelStation()
	
	
	//suspend fun getFuelSummaries(): List<FuelSummaryEntity>
	
	suspend fun getFuelSummary(fuelType: FuelType, date: String): SimpleResult<List<FuelSummaryEntity>>
	suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity)
	suspend fun deleteAllFuelSummaries()
	
	
	suspend fun getFuelHistory(limit: Int, offset: Int): SimpleResult<List<FuelHistoryEntity>>
	suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity)
	suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity)
	
}