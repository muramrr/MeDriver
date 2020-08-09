/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 15:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.datasource.local.fuel.IFuelLocalDataSource
import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelRemoteDataSource
import com.mmdev.me.driver.data.datasource.remote.vin.IVINRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.FuelRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.mappers.FuelDataMappersFacadeFactory
import com.mmdev.me.driver.data.repository.vin.VINRepositoryImpl
import com.mmdev.me.driver.domain.fuel.IFuelRepository
import com.mmdev.me.driver.domain.vin.IVINRepository
import org.koin.dsl.module

/**
 * module provides repositories instances
 * Repository often depends on local and remote DataSources
 * For example, @see [FuelRepositoryImpl]
 */


val RepositoryModule = module {

	single { provideVINRepository(_vinDataSourceRemote = get())}
	single { provideFuelRepository(_fuelDSRemote = get(), _fuelDSLocal = get()) }

}


fun provideVINRepository(_vinDataSourceRemote: IVINRemoteDataSource): IVINRepository =
	VINRepositoryImpl(_vinDataSourceRemote)

fun provideFuelRepository(_fuelDSRemote: IFuelRemoteDataSource,
                          _fuelDSLocal: IFuelLocalDataSource): IFuelRepository =
	FuelRepositoryImpl(_fuelDSRemote, _fuelDSLocal, FuelDataMappersFacadeFactory.create())