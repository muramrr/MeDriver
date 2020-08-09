/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 20:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import android.app.Application
import androidx.room.Room
import com.mmdev.me.driver.data.datasource.local.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * [DatabaseModule] provides RoomDatabase and DAO instances
 */

private const val DATABASE_NAME = "medriver_database"

val DatabaseModule = module {
	
	single { provideDatabase(androidApplication()) }
	single { provideFuelDao(db = get()) }
	//single { providePreferences(androidApplication()) }
}

//private fun providePreferences(app: Application): Preferences {
//	return BinaryPreferencesBuilder(app).name(PREFERENCES_NAME).build()
//}

private fun provideDatabase(app: Application): MeDriverRoomDatabase {
	return Room
		.databaseBuilder(app, MeDriverRoomDatabase::class.java, DATABASE_NAME)
		.fallbackToDestructiveMigration() // get correct db version if schema changed
		.build()
}

private fun provideFuelDao(db: MeDriverRoomDatabase): FuelDao = db.getFuelDao()