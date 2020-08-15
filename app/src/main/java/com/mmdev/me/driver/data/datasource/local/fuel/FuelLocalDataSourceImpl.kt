/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.08.2020 19:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel

import com.mmdev.me.driver.core.utils.logDebug
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * [IFuelLocalDataSource] implementation
 */

internal class FuelLocalDataSourceImpl(private val fuelDao: FuelDao) :
		BaseDataSource(), IFuelLocalDataSource {
	
	override suspend fun getFuelStationsAndPrices()=
		safeCall { fuelDao.getFuelPrices() }
		
	
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
	
	
	override suspend fun getFuelHistory(limit: Int, offset: Int): SimpleResult<List<FuelHistoryEntity>> =
		safeCall { fuelDao.getFuelHistory(limit, offset) }
	
	override suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity) =
		fuelDao.insertFuelHistoryRecord(fuelHistoryEntity).also {
			logDebug(TAG, "Adding History entry: id = ${fuelHistoryEntity.historyEntryId}")
		}
	
	override suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity) =
		fuelDao.deleteFuelHistorySingleRecord(fuelHistoryEntity).also {
			logDebug(TAG, "Deleting History entry: id = ${fuelHistoryEntity.historyEntryId}")
		}
}