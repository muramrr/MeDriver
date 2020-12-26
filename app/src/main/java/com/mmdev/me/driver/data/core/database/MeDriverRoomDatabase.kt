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

package com.mmdev.me.driver.data.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.datasource.fuel.history.local.dao.FuelHistoryDao
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.dao.FuelPricesDao
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity
import com.mmdev.me.driver.data.datasource.garage.dao.GarageDao
import com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.MaintenanceEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.dao.VehicleDao
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity

/**
 * Local [RoomDatabase]
 * Only 1 database instance is used in Application
 * That means if u clear the app data - all cached operations, which were not written to server
 * will be lost
 */

@Database(entities = [
	CachedOperation::class,
	FuelStationEntity::class,
	FuelPriceEntity::class,
	FuelSummaryEntity::class,
	FuelHistoryEntity::class,
	VehicleEntity::class,
	MaintenanceEntity::class
],
          version = 1,
          exportSchema = false)
abstract class MeDriverRoomDatabase: RoomDatabase() {
	
	companion object {
		const val CACHE_OPERATIONS_TABLE = "cached_operations"
		const val FUEL_HISTORY_TABLE = "fuel_history"
		const val FUEL_STATIONS_TABLE = "fuel_stations"
		const val FUEL_SUMMARY_TABLE = "fuel_summary"
		const val VEHICLES_TABLE = "vehicles"
		const val MAINTENANCE_HISTORY_TABLE = "vehicle_replaced_parts"
	}
	
	abstract fun getCacheDao(): CacheDao
	
	abstract fun getGarageDao(): GarageDao
	
	abstract fun getMaintenanceDao(): MaintenanceDao
	
	abstract fun getVehicleDao(): VehicleDao
	
	abstract fun getFuelHistoryDao(): FuelHistoryDao
	
	abstract fun getFuelPricesDao(): FuelPricesDao
	
}