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
	suspend fun getFuelStationsAndPrices(
		date: String,
		regionId: Int
	): SimpleResult<List<FuelStationAndPrices>>
	
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