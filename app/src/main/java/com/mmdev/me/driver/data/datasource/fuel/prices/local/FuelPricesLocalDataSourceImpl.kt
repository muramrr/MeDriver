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

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.dao.FuelPricesDao
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * [IFuelPricesLocalDataSource] implementation
 */

class FuelPricesLocalDataSourceImpl(private val dao: FuelPricesDao) :
		BaseDataSource(), IFuelPricesLocalDataSource {
	
	override suspend fun getFuelStationsAndPrices(
		date: String,
		regionId: Int
	): SimpleResult<List<FuelStationAndPrices>> = safeCall(TAG) { dao.getFuelPrices(date, regionId) }
		
	
	override suspend fun addFuelStationsAndPrices(
		fuelStationEntities: List<FuelStationEntity>,
		fuelPriceEntities: List<FuelPriceEntity>
	) = dao.insertFuelStationsAndPrices(fuelStationEntities, fuelPriceEntities).also {
		fuelStationEntities.forEach { logDebug(TAG, "Adding Station: ${it.slug}") }
		fuelPriceEntities.forEach {
			logDebug(TAG, "Adding Price: station = ${it.fuelStationId}, " +
			              "price = ${it.price}, type = ${it.typeCode}"
			)
		}
	}
	
	override suspend fun deleteAllFuelStations() = dao.deleteAllFuelStations()
	
	override suspend fun getFuelSummary(
		fuelType: FuelType,
		date: String
	): SimpleResult<List<FuelSummaryEntity>> = safeCall(TAG) { dao.getFuelSummary(fuelType.code, date) }
		
	
	override suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity) =
		dao.insertFuelSummary(fuelSummaryEntity).also {
			logDebug(TAG, "Adding Summary: ${fuelSummaryEntity.updatedDate}")
		}
	
	override suspend fun deleteAllFuelSummaries() = dao.deleteAllFuelSummaries()
	
}