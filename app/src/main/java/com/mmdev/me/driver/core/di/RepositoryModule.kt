/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 15:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelDataSourceRemote
import com.mmdev.me.driver.data.datasource.remote.vin.IVINDataSourceRemote
import com.mmdev.me.driver.data.repository.FuelRepositoryImpl
import com.mmdev.me.driver.data.repository.VINRepositoryImpl
import com.mmdev.me.driver.domain.fuel.IFuelRepository
import com.mmdev.me.driver.domain.vin.IVINRepository
import org.koin.dsl.module

/**
 * This is the documentation block about the class
 */


val RepositoryModule = module {

	single { provideVINRepository(_vinDataSourceRemote = get())}
	single { provideFuelRepository(_fuelDSRemote = get()) }

}


fun provideVINRepository(_vinDataSourceRemote: IVINDataSourceRemote): IVINRepository =
	VINRepositoryImpl(_vinDataSourceRemote)

fun provideFuelRepository(_fuelDSRemote: IFuelDataSourceRemote): IFuelRepository =
	FuelRepositoryImpl(_fuelDSRemote)