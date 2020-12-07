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

package com.mmdev.me.driver.data.datasource.fuel.history.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity

/**
 * Dao interface responsible to retrieve cached history entries from database
 *
 * Primary used in [com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource]
 */

@Dao
interface FuelHistoryDao {
	
	@Query(
		"""
		SELECT * FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		ORDER BY date DESC
		LIMIT :limit OFFSET :offset
	"""
	)
	suspend fun getVehicleFuelHistory(vin: String, limit: Int, offset: Int): List<FuelHistoryEntity>
	
	@Query(
		"""
		SELECT * FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		ORDER BY date DESC
		LIMIT 1
	"""
	)
	suspend fun getVehicleFuelHistoryFirst(vin: String): FuelHistoryEntity?
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE} WHERE dateAdded = :key")
	suspend fun getRecordById(key: Long): FuelHistoryEntity?
	
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertFuelHistoryEntity(fuelHistoryEntity: FuelHistoryEntity)
	
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun importFuelHistory(fuelHistoryToImport: List<FuelHistoryEntity>)
	
	@Delete
	suspend fun deleteFuelHistoryEntity(fuelHistoryEntity: FuelHistoryEntity)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE}")
	suspend fun clearHistory()
	
}