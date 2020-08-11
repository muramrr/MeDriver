/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 20:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel

import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * [IFuelLocalDataSource] implementation
 */

internal class FuelLocalDataSourceImpl(private val fuelDao: FuelDao) : IFuelLocalDataSource {
	
	override suspend fun getFuelProvidersAndPrices(date: String)=
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
	
	override suspend fun getFuelSummary(fuelType: FuelType, date: String):
		SimpleResult<List<FuelSummaryEntity>> =
		try {
			ResultState.Success(fuelDao.getFuelSummary(fuelType.code, date))
		}
		catch (t: Throwable) {
			ResultState.Failure(t)
		}
		
	
	override suspend fun addFuelSummary(fuelSummaryEntity: FuelSummaryEntity) =
		fuelDao.insertFuelSummary(fuelSummaryEntity)
	
	override suspend fun deleteAllFuelSummaries() = fuelDao.deleteAllFuelSummaries()
	
}