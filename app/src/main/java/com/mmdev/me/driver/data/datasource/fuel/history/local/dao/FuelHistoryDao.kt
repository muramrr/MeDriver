/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 20:05
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
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity

/**
 * Dao interface to "talk" with MeDriverRoomDatabase related to [FuelHistoryLocalDataSource]
 */

@Dao
interface FuelHistoryDao {
	
	@Query("SELECT * FROM fuel_history ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
	suspend fun getFuelHistory(limit: Int, offset: Int): List<FuelHistoryEntity>
	
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertFuelHistoryEntity(fuelHistoryEntity: FuelHistoryEntity)
	
	@Delete
	suspend fun deleteFuelHistoryEntity(fuelHistoryEntity: FuelHistoryEntity)
	
	@Query("DELETE FROM fuel_history")
	suspend fun clearHistory()
	
}