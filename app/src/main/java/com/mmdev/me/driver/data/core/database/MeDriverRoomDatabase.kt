/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 01:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
import com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
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
	VehicleSparePartEntity::class
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
	
	abstract fun getMaintenanceDao(): MaintenanceDao
	
	abstract fun getVehicleDao(): VehicleDao
	
	abstract fun getFuelHistoryDao(): FuelHistoryDao
	
	abstract fun getFuelPricesDao(): FuelPricesDao
	
}