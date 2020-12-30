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

import com.mmdev.me.driver.data.datasource.fuel.history.local.FuelHistoryLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.FuelPricesLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.prices.local.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.datasource.garage.GarageLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.garage.IGarageLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.local.MaintenanceLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.local.VehicleLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.vin.local.IVinLocalDataSource
import com.mmdev.me.driver.data.datasource.vin.local.VinLocalDataSourceImpl
import org.koin.dsl.module


/**
 * [DataSourceLocalModule] provides LocalDataSource instances
 */

val DataSourceLocalModule = module {
	
	factory<IGarageLocalDataSource> { GarageLocalDataSourceImpl(dao = get()) }
	
	factory<IMaintenanceLocalDataSource> { MaintenanceLocalDataSourceImpl(dao = get(), cache = get()) }
	
	factory<IVehicleLocalDataSource> { VehicleLocalDataSourceImpl(dao = get(), cache = get()) }
	factory<IVinLocalDataSource> { VinLocalDataSourceImpl() } //todo
	
	factory<IFuelPricesLocalDataSource> { FuelPricesLocalDataSourceImpl(dao = get()) }
	factory<IFuelHistoryLocalDataSource> { FuelHistoryLocalDataSourceImpl(dao = get(), cache = get()) }
	
}