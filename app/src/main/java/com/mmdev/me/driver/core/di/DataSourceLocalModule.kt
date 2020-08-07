/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 18:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.datasource.local.fuel.FuelLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.local.fuel.IFuelLocalDataSource
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import org.koin.dsl.module


/**
 * module provides LocalDataSource instances
 */

val DataSourceLocalModule = module {
	
	single { provideFuelDSLocal(fuelDao = get()) }
	
}


fun provideFuelDSLocal(fuelDao: FuelDao) : IFuelLocalDataSource = FuelLocalDataSourceImpl(fuelDao)