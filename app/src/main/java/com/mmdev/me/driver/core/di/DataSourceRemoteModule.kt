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

import com.mmdev.me.driver.data.datasource.fuel.history.remote.FuelHistoryRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.history.remote.IFuelHistoryRemoteDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.FuelPricesRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.IFuelPricesRemoteDataSource
import com.mmdev.me.driver.data.datasource.maintenance.remote.IMaintenanceRemoteDataSource
import com.mmdev.me.driver.data.datasource.maintenance.remote.MaintenanceRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.user.auth.AuthCollector
import com.mmdev.me.driver.data.datasource.user.auth.FirebaseAuthDataSourceImpl
import com.mmdev.me.driver.data.datasource.user.auth.IFirebaseAuthDataSource
import com.mmdev.me.driver.data.datasource.user.remote.IUserRemoteDataSource
import com.mmdev.me.driver.data.datasource.user.remote.UserRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.vehicle.remote.IVehicleRemoteDataSource
import com.mmdev.me.driver.data.datasource.vehicle.remote.VehicleRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.vin.remote.IVinRemoteDataSource
import com.mmdev.me.driver.data.datasource.vin.remote.VinRemoteDataSourceImpl
import org.koin.dsl.module


/**
 * [DataSourceRemoteModule] provides RemoteDataSource instances
 */

val DataSourceRemoteModule = module {
	
	single { AuthCollector(auth = get()) }
	
	factory<IUserRemoteDataSource> { UserRemoteDataSourceImpl(fs = get()) }
	factory<IFirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(auth = get()) }
	
	
	
	
	factory<IMaintenanceRemoteDataSource> { MaintenanceRemoteDataSourceImpl(fs = get()) }
	
	
	factory<IVehicleRemoteDataSource> { VehicleRemoteDataSourceImpl(fs = get()) }
	factory<IVinRemoteDataSource> { VinRemoteDataSourceImpl(vinCodeApi = get()) }
	
	factory<IFuelPricesRemoteDataSource> { FuelPricesRemoteDataSourceImpl(fuelApi = get()) }
	factory<IFuelHistoryRemoteDataSource> { FuelHistoryRemoteDataSourceImpl(fs = get()) }
	
	
	
}