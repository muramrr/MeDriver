/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.local.dao

import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPricesEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity

/**
 * Dao interface to "talk" with MeDriverRoomDatabase related to [FuelPricesLocalDataSource]
 */

@Dao
interface FuelPricesDao {
	
	@Transaction
	@Query("SELECT * FROM fuel_stations WHERE updatedDate = :date")
	suspend fun getFuelPrices(date: String): List<FuelStationAndPricesEntity>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelStation(fuelStationEntity: FuelStationEntity)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelPrice(fuelPrice: FuelPriceEntity)
	
	@Query("DELETE FROM fuel_stations")
	suspend fun deleteAllFuelStations()
	
	
	@VisibleForTesting
	@Query("SELECT * FROM fuel_summary")
	suspend fun getFuelSummaries(): List<FuelSummaryEntity>
	
	@Query("SELECT * FROM fuel_summary WHERE type = :fuelType AND updatedDate = :updatedDate")
	suspend fun getFuelSummary(fuelType: Int, updatedDate: String): List<FuelSummaryEntity>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelSummary(fuelSummaryEntity: FuelSummaryEntity)
	
	@Query("DELETE FROM fuel_summary")
	suspend fun deleteAllFuelSummaries()
	
}