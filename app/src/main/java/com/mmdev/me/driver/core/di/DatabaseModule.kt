/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import android.app.Application
import androidx.room.Room
import com.mmdev.me.driver.data.datasource.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao
import com.mmdev.me.driver.data.datasource.fuel.prices.local.dao.FuelPricesDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * [DatabaseModule] provides RoomDatabase, DAO instances, SharedPrefs
 */

private const val DATABASE_NAME = "medriver_database"

val DatabaseModule = module {
	
	single { provideDatabase(androidApplication()) }
	single { provideFuelPricesDao(db = get()) }
	single { provideFuelHistoryDao(db = get()) }
	//single { providePreferences(androidApplication()) }
}

//private fun providePreferences(app: Application): Preferences {
//	return BinaryPreferencesBuilder(app).name(PREFERENCES_NAME).build()
//}

private fun provideDatabase(app: Application): MeDriverRoomDatabase {
	return Room
		.databaseBuilder(app, MeDriverRoomDatabase::class.java, DATABASE_NAME)
		.build()
}

private fun provideFuelPricesDao(db: MeDriverRoomDatabase): FuelPricesDao = db.getFuelPricesDao()
private fun provideFuelHistoryDao(db: MeDriverRoomDatabase): FuelHistoryDao = db.getFuelHistoryDao()