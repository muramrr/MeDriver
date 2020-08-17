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

import com.mmdev.me.driver.data.datasource.local.fuel.history.FuelHistoryLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.local.fuel.history.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.local.fuel.prices.FuelPricesLocalDataSourceImpl
import com.mmdev.me.driver.data.datasource.local.fuel.prices.IFuelPricesLocalDataSource
import org.koin.dsl.module


/**
 * [DataSourceLocalModule] provides LocalDataSource instances
 */

val DataSourceLocalModule = module {
	
	single<IFuelPricesLocalDataSource> { FuelPricesLocalDataSourceImpl(fuelDao = get()) }
	single<IFuelHistoryLocalDataSource> { FuelHistoryLocalDataSourceImpl(fuelDao = get()) }
	
}