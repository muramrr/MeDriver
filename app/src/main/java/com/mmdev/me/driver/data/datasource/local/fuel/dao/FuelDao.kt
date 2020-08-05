/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 18:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmdev.me.driver.domain.fuel.FuelModel

/**
 *
 */

@Dao
interface FuelDao {
	
	@Query("SELECT * FROM fuelInfo")
	suspend fun getAllFuelModels(): FuelModel
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelModel(fuelModel: FuelModel)
	
	@Query(value = "SELECT * FROM fuelInfo WHERE retrievedDate = :date")
	suspend fun getFuelModelByDate(date: String): FuelModel
	
	@Query("DELETE FROM fuelInfo")
	suspend fun deleteAll()
	
}