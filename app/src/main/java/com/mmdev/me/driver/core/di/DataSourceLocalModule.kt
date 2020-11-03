/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.11.2020 17:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.datasource.fuel.history.local.FuelHistoryLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.FuelPricesLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.prices.local.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.local.MaintenanceLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.user.local.IUserLocalDataSource
import com.mmdev.me.driver.data.datasource.user.local.UserLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.local.VehicleLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.vin.local.IVinLocalDataSource
import com.mmdev.me.driver.data.datasource.vin.local.VinLocalDataSourceImpl
import org.koin.dsl.module


/**
 * [DataSourceLocalModule] provides LocalDataSource instances
 */

val DataSourceLocalModule = module {
	
	single<IUserLocalDataSource> { UserLocalDataSourceImpl(prefs = MedriverApp.prefs, dataManipulator = get()) }
	
	single<IMaintenanceLocalDataSource> { MaintenanceLocalDataSourceImpl(dao = get()) }
	
	single<IVehicleLocalDataSource> { VehicleLocalDataSourceImpl(dao = get()) }
	single<IVinLocalDataSource> { VinLocalDataSourceImpl() }
	
	single<IFuelPricesLocalDataSource> { FuelPricesLocalDataSourceImpl(dao = get()) }
	single<IFuelHistoryLocalDataSource> { FuelHistoryLocalDataSourceImpl(dao = get()) }
	
}