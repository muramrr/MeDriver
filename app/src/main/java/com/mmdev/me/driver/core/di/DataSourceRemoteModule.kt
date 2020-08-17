/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.datasource.remote.fuel.FuelPricesRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelPricesRemoteDataSource
import com.mmdev.me.driver.data.datasource.remote.vin.IVINRemoteDataSource
import com.mmdev.me.driver.data.datasource.remote.vin.VINRemoteDataSourceImpl
import org.koin.dsl.module


/**
 * [DataSourceRemoteModule] provides RemoteDataSource instances
 */

val DataSourceRemoteModule = module {

	single<IVINRemoteDataSource> { VINRemoteDataSourceImpl(vinCodeApi = get()) }
	single<IFuelPricesRemoteDataSource> { FuelPricesRemoteDataSourceImpl(fuelApi = get()) }
	
}