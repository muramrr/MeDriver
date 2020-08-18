/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.08.2020 18:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.dao

import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mmdev.me.driver.data.datasource.local.fuel.history.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelSummaryEntity

/**
 * Dao interface to "talk" with MeDriverRoomDatabase
 */

@Dao
interface FuelDao {
	
	@Transaction
	@Query("SELECT * FROM fuel_providers WHERE updatedDate = :date")
	suspend fun getFuelPrices(date: String): List<FuelStationAndPrices>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelProvider(fuelStationEntity: FuelStationEntity)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelPrice(fuelPrice: FuelPriceEntity)
	
	@Query("DELETE FROM fuel_providers")
	suspend fun deleteAllFuelProviders()
	
	
	
	
	@VisibleForTesting
	@Query("SELECT * FROM fuel_summary")
	suspend fun getFuelSummaries(): List<FuelSummaryEntity>
	
	@Query("SELECT * FROM fuel_summary WHERE type = :fuelType AND updatedDate = :updatedDate")
	suspend fun getFuelSummary(fuelType: Int, updatedDate: String): List<FuelSummaryEntity>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelSummary(fuelSummaryEntity: FuelSummaryEntity)
	
	@Query("DELETE FROM fuel_summary")
	suspend fun deleteAllFuelSummaries()
	
	
	
	
	@Query("SELECT * FROM fuel_history LIMIT :limit OFFSET :offset")
	suspend fun getFuelHistory(limit: Int, offset: Int): List<FuelHistoryEntity>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelHistoryRecord(fuelHistoryEntity: FuelHistoryEntity)
	
	@Delete
	suspend fun deleteFuelHistorySingleRecord(fuelHistoryEntity: FuelHistoryEntity)
	
	@Query("DELETE FROM fuel_history")
	suspend fun clearHistory()
}