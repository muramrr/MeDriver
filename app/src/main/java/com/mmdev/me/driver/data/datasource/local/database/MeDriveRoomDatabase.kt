/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 17:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.domain.fuel.FuelModel

/**
 *
 */

@Database(entities = arrayOf(FuelModel::class), version = 1, exportSchema = false)
abstract class MeDriveRoomDatabase : RoomDatabase() {
	
	abstract fun getFuelDao(): FuelDao
	
}