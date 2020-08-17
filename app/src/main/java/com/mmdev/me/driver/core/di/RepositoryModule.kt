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

import com.mmdev.me.driver.data.repository.fuel.history.FuelHistoryRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.data.repository.fuel.prices.FuelPricesRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.data.repository.vin.VINRepositoryImpl
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.vin.IVINRepository
import org.koin.dsl.module

/**
 * [RepositoryModule] provides repositories instances
 * Repository often depends on local and remote DataSources
 * For example, @see [FuelPricesRepositoryImpl]
 */


val RepositoryModule = module {

	single<IVINRepository> { VINRepositoryImpl(dataSourceRemote = get())}
	single<IFuelPricesRepository> {
		FuelPricesRepositoryImpl(
			dataSourceLocal = get(), dataSourceRemote = get(), mappers = FuelPriceMappersFacade()
		)
	}
	single<IFuelHistoryRepository> {
		FuelHistoryRepositoryImpl(dataSourceLocal = get(), mappers = FuelHistoryMappersFacade())
	}

}

