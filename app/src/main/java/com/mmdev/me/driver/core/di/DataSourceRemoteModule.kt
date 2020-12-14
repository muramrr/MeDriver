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

import com.mmdev.me.driver.data.datasource.billing.BillingDataSource
import com.mmdev.me.driver.data.datasource.fetching.FetchingDataSource
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
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


/**
 * [DataSourceRemoteModule] provides RemoteDataSource instances
 */

val DataSourceRemoteModule = module {
	
	single { AuthCollector(auth = get()) }
	
	single { BillingDataSource(context = androidApplication()) }
	
	factory<IUserRemoteDataSource> { UserRemoteDataSourceImpl(fs = get()) }
	factory<IFirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(auth = get()) }
	
	factory { FetchingDataSource(fs = get()) }
	
	
	factory<IMaintenanceServerDataSource> { MaintenanceServerDataSourceImpl(fs = get()) }
	
	
	factory<IVehicleServerDataSource> { VehicleServerDataSourceImpl(fs = get()) }
	factory<IVinApiDataSource> { VinApiDataSourceImpl(vinCodeApi = get()) }
	
	factory<IFuelPricesApiDataSource> { FuelPricesApiDataSourceImpl(fuelApi = get()) }
	factory<IFuelHistoryServerDataSource> { FuelHistoryServerDataSourceImpl(fs = get()) }
	
	
	
}