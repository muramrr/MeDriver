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

package com.mmdev.me.driver.data.datasource.fuel.prices.local.dao

import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity

/**
 * Dao interface to "talk" with MeDriverRoomDatabase related to [FuelPricesLocalDataSource]
 */

@Dao
interface FuelPricesDao {
	
	@Transaction
	@Query("""
		SELECT * FROM ${MeDriverRoomDatabase.FUEL_STATIONS_TABLE}
		WHERE updatedDate = :date AND regionId = :regionId
	""")
	suspend fun getFuelPrices(date: String, regionId: Int): List<FuelStationAndPrices>
	
	@Transaction
	suspend fun insertFuelStationsAndPrices(
		fuelStations: List<FuelStationEntity>, fuelPrices: List<FuelPriceEntity>) {
		insertFuelStations(fuelStations)
		insertFuelPrices(fuelPrices)
	}
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertFuelStations(fuelStations: List<FuelStationEntity>)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertFuelPrices(fuelPrices: List<FuelPriceEntity>)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.FUEL_STATIONS_TABLE}")
	suspend fun deleteAllFuelStations()
	
	
	@VisibleForTesting
	@Query("SELECT * FROM ${MeDriverRoomDatabase.FUEL_SUMMARY_TABLE}")
	suspend fun getFuelSummaries(): List<FuelSummaryEntity>
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.FUEL_SUMMARY_TABLE} WHERE typeCode = :fuelType AND updatedDate = :updatedDate")
	suspend fun getFuelSummary(fuelType: Int, updatedDate: String): List<FuelSummaryEntity>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelSummary(fuelSummaryEntity: FuelSummaryEntity)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.FUEL_SUMMARY_TABLE}")
	suspend fun deleteAllFuelSummaries()
	
}