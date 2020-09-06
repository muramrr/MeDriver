/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.modules

import androidx.room.Room
import com.mmdev.me.driver.data.datasource.database.MeDriverRoomDatabase
import org.koin.dsl.module

/**
 * In-Memory Room Database definition
 */

val DatabaseTestModule = module (override = true) {
	
	single {
		// In-Memory database config
		Room.inMemoryDatabaseBuilder(get(), MeDriverRoomDatabase::class.java)
			.allowMainThreadQueries()
			.build()
	}
}
