/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 17:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel

import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderAndPrices
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 *
 */

internal class FuelLocalDataSourceImpl(private val fuelDao: FuelDao) : IFuelLocalDataSource {
	
	override suspend fun getFuelProvidersAndPrices(date: String): SimpleResult<List<FuelProviderAndPrices>> =
		try {
			ResultState.Success(fuelDao.getFuelPrices(date))
		}
		catch (t: Throwable) {
			ResultState.Failure(t)
		}
		
	
	override suspend fun addFuelProvider(fuelProviderEntity: FuelProviderEntity) =
		fuelDao.insertFuelProvider(fuelProviderEntity)
	
	override suspend fun addFuelPrice(fuelPrice: FuelPriceEntity) =
		fuelDao.insertFuelPrice(fuelPrice)
	
	override suspend fun deleteAllFuelProviders() = fuelDao.deleteAllFuelProviders()
	
	override suspend fun getFuelSummaryByDateAndType(fuelType: FuelType,
	                                                 updatedDate: String): SimpleResult<List<FuelSummaryEntity>> =
		try {
			ResultState.Success(fuelDao.getFuelSummaryByDateAndType(fuelType.code, updatedDate))
		}
		catch (t: Throwable) {
			ResultState.Failure(t)
		}
		
	
	override suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity) =
		fuelDao.insertFuelSummary(fuelSummaryEntity)
	
	override suspend fun deleteAllFuelSummaries() = fuelDao.deleteAllFuelSummaries()
	
}