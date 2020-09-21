/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 17:05
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
import androidx.room.Transaction
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.VehicleWithFuelHistory

/**
 * Dao interface to "talk" with MeDriverRoomDatabase related to [FuelHistoryLocalDataSource]
 */

@Dao
interface FuelHistoryDao {
	
	@Transaction
	@Query("""
		SELECT * FROM vehicle INNER JOIN fuel_history
		WHERE vin = :vin
		ORDER BY timestamp DESC
		LIMIT:limit OFFSET :offset
	""")
	suspend fun getVehicleFuelHistory(vin: String, limit: Int, offset: Int): VehicleWithFuelHistory
	
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertFuelHistoryEntity(fuelHistoryEntity: FuelHistoryEntity)
	
	@Delete
	suspend fun deleteFuelHistoryEntity(fuelHistoryEntity: FuelHistoryEntity)
	
	@Query("DELETE FROM fuel_history")
	suspend fun clearHistory()
	
}