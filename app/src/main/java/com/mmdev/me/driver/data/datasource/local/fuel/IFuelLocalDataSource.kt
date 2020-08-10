/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 17:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel

import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * Fuel DataSource which works with local database
 */

interface IFuelLocalDataSource {
	
	suspend fun getFuelPrices(fuelType: FuelType, date: String): SimpleResult<List<FuelPriceEntity>>
	
	suspend fun addFuelPrice(fuelPrice: FuelPriceEntity)
	
	suspend fun deleteAllFuelPrices()
	
	
	
	//suspend fun getFuelSummaries(): List<FuelSummaryEntity>
	
	suspend fun getFuelSummary(fuelType: FuelType, date: String): SimpleResult<List<FuelSummaryEntity>>
	
	suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity)
	
	suspend fun deleteAllFuelSummaries()
	
}