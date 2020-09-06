/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.datasource.fuel.prices.remote.FuelPricesRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.IFuelPricesRemoteDataSource
import com.mmdev.me.driver.data.datasource.vin.remote.IVINRemoteDataSource
import com.mmdev.me.driver.data.datasource.vin.remote.VINRemoteDataSourceImpl
import org.koin.dsl.module


/**
 * [DataSourceRemoteModule] provides RemoteDataSource instances
 */

val DataSourceRemoteModule = module {

	single<IVINRemoteDataSource> { VINRemoteDataSourceImpl(vinCodeApi = get()) }
	single<IFuelPricesRemoteDataSource> { FuelPricesRemoteDataSourceImpl(fuelApi = get()) }
	
}