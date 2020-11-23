/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.11.2020 16:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.repository.auth.AuthFlowProviderImpl
import com.mmdev.me.driver.data.repository.auth.mappers.UserMappersFacade
import com.mmdev.me.driver.data.repository.fetching.FetchingRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.history.FuelHistoryRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.prices.FuelPricesRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.data.repository.maintenance.MaintenanceRepositoryImpl
import com.mmdev.me.driver.data.repository.settings.SettingsRepositoryImpl
import com.mmdev.me.driver.data.repository.vehicle.VehicleRepositoryImpl
import com.mmdev.me.driver.domain.fetching.IFetchingRepository
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.user.auth.IAuthFlowProvider
import com.mmdev.me.driver.domain.user.auth.ISettingsRepository
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import org.koin.dsl.module

/**
 * [RepositoryModule] provides repositories instances
 * Repository basically depends on local and remote DataSources and specific mappers facade
 * For example, @see [FuelPricesRepositoryImpl]
 */


val RepositoryModule = module {
	
	single<IFetchingRepository> {
		FetchingRepositoryImpl(
			vehicleLocalDS = get(),
			vehicleRemoteDS = get(),
			mappers = get()
		)
	}
	
	single<ISettingsRepository> { SettingsRepositoryImpl(authDataSource = get()) }
	
	
	
	single<IAuthFlowProvider> {
		AuthFlowProviderImpl(
			authCollector = get(),
			userLocalDataSource = get(),
			userRemoteDataSource = get(),
			mappers = UserMappersFacade()
		)
	}
	
	single<IMaintenanceRepository> {
		MaintenanceRepositoryImpl(
			localDataSource = get(),
			remoteDataSource = get(),
			mappers = get()
		)
	}
	
	
	
	single<IVehicleRepository> {
		VehicleRepositoryImpl(
			localDataSource = get(),
			remoteDataSource = get(),
			localVinDecoder = get(),
			remoteVinDecoder = get(),
			mappers = get()
		)
	}
	
	
	
	single<IFuelPricesRepository> {
		FuelPricesRepositoryImpl(
			localDataSource = get(),
			remoteDataSource = get(),
			mappers = FuelPriceMappersFacade()
		)
	}
	single<IFuelHistoryRepository> {
		FuelHistoryRepositoryImpl(
			localDataSource = get(),
			remoteDataSource = get(),
			mappers = get()
		)
	}
	
	

}

