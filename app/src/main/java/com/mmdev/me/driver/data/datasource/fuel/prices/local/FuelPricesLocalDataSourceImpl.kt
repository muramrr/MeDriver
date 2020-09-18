/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.09.2020 17:59
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
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPricesEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * [IFuelPricesLocalDataSource] implementation
 */

internal class FuelPricesLocalDataSourceImpl(private val dao: FuelPricesDao) :
		BaseDataSource(), IFuelPricesLocalDataSource {
	
	override suspend fun getFuelStationsAndPrices(date: String):
			SimpleResult<List<FuelStationAndPricesEntity>> = safeCall { dao.getFuelPrices(date) }
		
	
	override suspend fun addFuelStation(fuelStationEntity: FuelStationEntity) =
		dao.insertFuelStation(fuelStationEntity).also {
			logDebug(TAG, "Adding Station: ${fuelStationEntity.slug}")
		}
	
	
	override suspend fun addFuelPrice(fuelPrice: FuelPriceEntity) =
		dao.insertFuelPrice(fuelPrice).also {
			logDebug(TAG, "Adding Price: " +
			              "station = ${fuelPrice.fuelStationId}, " +
			              "price = ${fuelPrice.price}, " +
			              "type = ${fuelPrice.type}")
		}
	
	override suspend fun deleteAllFuelStation() = dao.deleteAllFuelStations()
	
	override suspend fun getFuelSummary(fuelType: FuelType, date: String):
		SimpleResult<List<FuelSummaryEntity>> = safeCall { dao.getFuelSummary(fuelType.code, date) }
		
	
	override suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity) =
		dao.insertFuelSummary(fuelSummaryEntity).also {
			logDebug(TAG, "Adding Summary: ${fuelSummaryEntity.updatedDate}")
		}
	
	override suspend fun deleteAllFuelSummaries() = dao.deleteAllFuelSummaries()
	
}