/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.core.di

import android.app.Application
import androidx.room.Room
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao
import com.mmdev.me.driver.data.datasource.fuel.prices.local.dao.FuelPricesDao
import com.mmdev.me.driver.data.datasource.garage.dao.GarageDao
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
	factory { provideGarageDao(db = get()) }
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
private fun provideGarageDao(db: MeDriverRoomDatabase): GarageDao = db.getGarageDao()
private fun provideMaintenanceDao(db: MeDriverRoomDatabase): MaintenanceDao = db.getMaintenanceDao()
private fun provideVehicleDao(db: MeDriverRoomDatabase): VehicleDao = db.getVehicleDao()
private fun provideFuelPricesDao(db: MeDriverRoomDatabase): FuelPricesDao = db.getFuelPricesDao()
private fun provideFuelHistoryDao(db: MeDriverRoomDatabase): FuelHistoryDao = db.getFuelHistoryDao()