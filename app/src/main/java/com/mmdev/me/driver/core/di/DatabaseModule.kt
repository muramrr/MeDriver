/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 18:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import android.app.Application
import androidx.room.Room
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao
import com.mmdev.me.driver.data.datasource.fuel.prices.local.dao.FuelPricesDao
import com.mmdev.me.driver.data.datasource.home.dao.HomeDao
import com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao
import com.mmdev.me.driver.data.datasource.vehicle.local.dao.VehicleDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * [DatabaseModule] provides RoomDatabase, DAO instances, SharedPrefs
 */

private const val DATABASE_NAME = "medriver_database"

val DatabaseModule = module {
	
	single { provideDatabase(androidApplication()) }
	
	factory { provideCacheDao(db = get()) }
	factory { provideHomeDao(db = get()) }
	factory { provideMaintenanceDao(db = get()) }
	factory { provideVehicleDao(db = get()) }
	factory { provideFuelPricesDao(db = get()) }
	factory { provideFuelHistoryDao(db = get()) }
	
}

private fun provideDatabase(app: Application): MeDriverRoomDatabase {
	return Room
		.databaseBuilder(app, MeDriverRoomDatabase::class.java, DATABASE_NAME)
		.build()
}

private fun provideCacheDao(db: MeDriverRoomDatabase): CacheDao = db.getCacheDao()
private fun provideHomeDao(db: MeDriverRoomDatabase): HomeDao = db.getHomeDao()
private fun provideMaintenanceDao(db: MeDriverRoomDatabase): MaintenanceDao = db.getMaintenanceDao()
private fun provideVehicleDao(db: MeDriverRoomDatabase): VehicleDao = db.getVehicleDao()
private fun provideFuelPricesDao(db: MeDriverRoomDatabase): FuelPricesDao = db.getFuelPricesDao()
private fun provideFuelHistoryDao(db: MeDriverRoomDatabase): FuelHistoryDao = db.getFuelHistoryDao()