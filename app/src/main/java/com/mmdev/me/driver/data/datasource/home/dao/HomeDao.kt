/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.11.2020 21:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.home.dao

import androidx.room.Dao
import androidx.room.Query
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity

/**
 *
 */

@Dao
interface HomeDao {
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.VEHICLES_TABLE}")
	suspend fun getAllVehicles(): List<VehicleEntity>
	
}