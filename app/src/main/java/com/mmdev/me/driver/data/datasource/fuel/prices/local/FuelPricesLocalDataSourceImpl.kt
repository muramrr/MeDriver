/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.11.2020 19:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.local

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.core.base.BaseDataSource
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
		fuelStationEntities: List<FuelStationEntity>, fuelPriceEntities: List<FuelPriceEntity>
	) = dao.insertFuelStationsAndPrices(fuelStationEntities, fuelPriceEntities).also {
		fuelStationEntities.forEach { logDebug(TAG, "Adding Station: ${it.slug}") }
		fuelPriceEntities.forEach {
			logDebug(TAG, "Adding Price: station = ${it.fuelStationId}, " +
			              "price = ${it.price}, type = ${it.typeCode}"
			)
		}
	}
	
	override suspend fun deleteAllFuelStations() = dao.deleteAllFuelStations()
	
	override suspend fun getFuelSummary(fuelType: FuelType, date: String):
		SimpleResult<List<FuelSummaryEntity>> = safeCall(TAG) { dao.getFuelSummary(fuelType.code, date) }
		
	
	override suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity) =
		dao.insertFuelSummary(fuelSummaryEntity).also {
			logDebug(TAG, "Adding Summary: ${fuelSummaryEntity.updatedDate}")
		}
	
	override suspend fun deleteAllFuelSummaries() = dao.deleteAllFuelSummaries()
	
}