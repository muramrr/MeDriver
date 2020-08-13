/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.08.20 16:56
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity

/**
 * RoomDatabase
 */

@Database(entities = [
	FuelStationEntity::class,
	FuelPriceEntity::class,
	FuelSummaryEntity::class
],
          version = 1,
          exportSchema = false)
abstract class MeDriverRoomDatabase : RoomDatabase() {
	
	abstract fun getFuelDao(): FuelDao
	
}