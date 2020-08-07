/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 16:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.datasource.remote.api.FuelApi
import com.mmdev.me.driver.data.datasource.remote.api.VINCodeApi
import com.mmdev.me.driver.data.datasource.remote.fuel.FuelRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelRemoteDataSource
import com.mmdev.me.driver.data.datasource.remote.vin.IVINRemoteDataSource
import com.mmdev.me.driver.data.datasource.remote.vin.VINRemoteDataSourceImpl
import org.koin.dsl.module


/**
 * module provides RemoteDataSource instances
 */

val DataSourceRemoteModule = module {

	single { provideVinDSRemote(vinCodeApi = get()) }
	single { provideFuelDSRemote(fuelApi = get()) }


}

fun provideVinDSRemote(vinCodeApi: VINCodeApi) : IVINRemoteDataSource =
	VINRemoteDataSourceImpl(vinCodeApi)

fun provideFuelDSRemote(fuelApi: FuelApi) : IFuelRemoteDataSource =
	FuelRemoteDataSourceImpl(fuelApi)