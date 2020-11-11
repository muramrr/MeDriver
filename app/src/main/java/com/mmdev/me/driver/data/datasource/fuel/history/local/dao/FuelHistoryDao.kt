/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.11.2020 18:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
 * Primary used in [com.mmdev.me.driver.data.datasource.fuel.history.local]
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
	
//	@Insert(onConflict = OnConflictStrategy.IGNORE)
//	suspend fun importFuelHistory(import: List<FuelHistoryEntity>)
	
	@Delete
	suspend fun deleteFuelHistoryEntity(fuelHistoryEntity: FuelHistoryEntity)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE}")
	suspend fun clearHistory()
	
}