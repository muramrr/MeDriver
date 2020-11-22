/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 01:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.local

import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * Fuel Prices DataSource which works with local database
 */

interface IFuelPricesLocalDataSource {
	
	/**
	 * Get stored fuel station prices from database
	 * Basically DAO logic here is to group [FuelStationEntity] to related pack of [FuelPriceEntity]
	 */
	suspend fun getFuelStationsAndPrices(date: String): SimpleResult<List<FuelStationAndPrices>>
	
	/**
	 * Add [FuelStationEntity] entries and [FuelPriceEntity] to database, nothing special here
	 */
	suspend fun addFuelStationsAndPrices(
		fuelStationEntities: List<FuelStationEntity>,
		fuelPriceEntities: List<FuelPriceEntity>
	)
	
	suspend fun deleteAllFuelStations()
	//suspend fun deleteAllFuelPrices()
	
	
	suspend fun getFuelSummary(fuelType: FuelType, date: String): SimpleResult<List<FuelSummaryEntity>>
	suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity)
	suspend fun deleteAllFuelSummaries()
	
	
}