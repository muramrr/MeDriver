/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.08.20 16:56
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel

import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * Fuel DataSource which works with local database
 */

interface IFuelLocalDataSource {
	
	suspend fun getFuelProvidersAndPrices(date: String): SimpleResult<List<FuelStationAndPrices>>
	
	suspend fun addFuelProvider(fuelStationEntity: FuelStationEntity)
	
	suspend fun addFuelPrice(fuelPrice: FuelPriceEntity)
	
	suspend fun deleteAllFuelProviders()
	
	
	//suspend fun getFuelSummaries(): List<FuelSummaryEntity>
	
	suspend fun getFuelSummary(fuelType: FuelType, date: String): SimpleResult<List<FuelSummaryEntity>>
	
	suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity)
	
	suspend fun deleteAllFuelSummaries()
	
}