/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 20:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.repository.fuel.FuelRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.mappers.FuelDataMappersFacadeFactory
import com.mmdev.me.driver.data.repository.vin.VINRepositoryImpl
import com.mmdev.me.driver.domain.fuel.IFuelRepository
import com.mmdev.me.driver.domain.vin.IVINRepository
import org.koin.dsl.module

/**
 * [RepositoryModule] provides repositories instances
 * Repository often depends on local and remote DataSources
 * For example, @see [FuelRepositoryImpl]
 */


val RepositoryModule = module {

	single<IVINRepository> { VINRepositoryImpl(dataSourceRemote = get())}
	single<IFuelRepository> {
		FuelRepositoryImpl(dataSourceLocal = get(),
		                   dataSourceRemote = get(),
		                   mappers = FuelDataMappersFacadeFactory.create())
	}

}

