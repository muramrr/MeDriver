/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.08.2020 18:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.prices

import com.mmdev.me.driver.core.utils.logDebug
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * [IFuelPricesLocalDataSource] implementation
 */

internal class FuelPricesLocalDataSourceImpl(private val fuelDao: FuelDao) :
		BaseDataSource(), IFuelPricesLocalDataSource {
	
	override suspend fun getFuelStationsAndPrices(date: String)=
		safeCall { fuelDao.getFuelPrices(date) }
		
	
	override suspend fun addFuelStation(fuelStationEntity: FuelStationEntity) =
		fuelDao.insertFuelProvider(fuelStationEntity).also {
			logDebug(TAG, "Adding Station: ${fuelStationEntity.slug}")
		}
	
	
	override suspend fun addFuelPrice(fuelPrice: FuelPriceEntity) =
		fuelDao.insertFuelPrice(fuelPrice).also {
			logDebug(TAG, "Adding Price: " +
			              "station = ${fuelPrice.fuelStationId}, price = ${fuelPrice.price}")
		}
	
	override suspend fun deleteAllFuelStation() = fuelDao.deleteAllFuelProviders()
	
	override suspend fun getFuelSummary(fuelType: FuelType, date: String):
		SimpleResult<List<FuelSummaryEntity>> =
		safeCall { fuelDao.getFuelSummary(fuelType.code, date) }
		
	
	override suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity) =
		fuelDao.insertFuelSummary(fuelSummaryEntity).also {
			logDebug(TAG, "Adding Summary: ${fuelSummaryEntity.updatedDate}")
		}
	
	override suspend fun deleteAllFuelSummaries() = fuelDao.deleteAllFuelSummaries()
	
}