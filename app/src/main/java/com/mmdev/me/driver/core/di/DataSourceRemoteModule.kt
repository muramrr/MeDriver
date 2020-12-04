/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.datasource.fuel.history.server.FuelHistoryServerDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.history.server.IFuelHistoryServerDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.api.FuelPricesApiDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.prices.api.IFuelPricesApiDataSource
import com.mmdev.me.driver.data.datasource.maintenance.server.IMaintenanceServerDataSource
import com.mmdev.me.driver.data.datasource.maintenance.server.MaintenanceServerDataSourceImpl
import com.mmdev.me.driver.data.datasource.user.auth.AuthCollector
import com.mmdev.me.driver.data.datasource.user.auth.FirebaseAuthDataSourceImpl
import com.mmdev.me.driver.data.datasource.user.auth.IFirebaseAuthDataSource
import com.mmdev.me.driver.data.datasource.user.remote.IUserRemoteDataSource
import com.mmdev.me.driver.data.datasource.user.remote.UserRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.vehicle.server.IVehicleServerDataSource
import com.mmdev.me.driver.data.datasource.vehicle.server.VehicleServerDataSourceImpl
import com.mmdev.me.driver.data.datasource.vin.api.IVinApiDataSource
import com.mmdev.me.driver.data.datasource.vin.api.VinApiDataSourceImpl
import org.koin.dsl.module


/**
 * [DataSourceRemoteModule] provides RemoteDataSource instances
 */

val DataSourceRemoteModule = module {
	
	single { AuthCollector(auth = get()) }
	
	factory<IUserRemoteDataSource> { UserRemoteDataSourceImpl(fs = get()) }
	factory<IFirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(auth = get()) }
	
	
	
	
	factory<IMaintenanceServerDataSource> { MaintenanceServerDataSourceImpl(fs = get()) }
	
	
	factory<IVehicleServerDataSource> { VehicleServerDataSourceImpl(fs = get()) }
	factory<IVinApiDataSource> { VinApiDataSourceImpl(vinCodeApi = get()) }
	
	factory<IFuelPricesApiDataSource> { FuelPricesApiDataSourceImpl(fuelApi = get()) }
	factory<IFuelHistoryServerDataSource> { FuelHistoryServerDataSourceImpl(fs = get()) }
	
	
	
}