/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.dao.FuelPricesDao
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity

/**
 * RoomDatabase
 */

@Database(entities = [
	FuelStationEntity::class,
	FuelPriceEntity::class,
	FuelSummaryEntity::class,
	FuelHistoryEntity::class
],
          version = 1,
          exportSchema = false)
abstract class MeDriverRoomDatabase : RoomDatabase() {
	
	abstract fun getFuelHistoryDao(): FuelHistoryDao
	
	abstract fun getFuelPricesDao(): FuelPricesDao
	
}