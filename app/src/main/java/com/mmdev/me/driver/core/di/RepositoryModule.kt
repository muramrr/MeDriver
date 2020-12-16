/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.repository.auth.AuthFlowProviderImpl
import com.mmdev.me.driver.data.repository.auth.mappers.UserMappers
import com.mmdev.me.driver.data.repository.fetching.FetchingRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.history.FuelHistoryRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.prices.FuelPricesRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.data.repository.home.HomeRepositoryImpl
import com.mmdev.me.driver.data.repository.maintenance.MaintenanceRepositoryImpl
import com.mmdev.me.driver.data.repository.settings.SettingsRepositoryImpl
import com.mmdev.me.driver.data.repository.vehicle.VehicleRepositoryImpl
import com.mmdev.me.driver.domain.fetching.IFetchingRepository
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.home.IHomeRepository
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.user.IAuthFlowProvider
import com.mmdev.me.driver.domain.user.ISettingsRepository
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import org.koin.dsl.module

/**
 * [RepositoryModule] provides repositories instances
 * Repository basically depends on local and remote DataSources and specific mappers facade
 * For example, @see [FuelPricesRepositoryImpl]
 */


val RepositoryModule = module {
	
	factory<IFetchingRepository> {
		FetchingRepositoryImpl(
			fetchingDataSource = get(),
			vehicleRepository = get(),
			downloader = get()
		)
	}
	
	factory<IAuthFlowProvider> {
		AuthFlowProviderImpl(
			authCollector = get(),
			userRemoteDataSource = get(),
			billingDataSource = get(),
			mappers = UserMappers()
		)
	}
	
	factory<IHomeRepository> {
		HomeRepositoryImpl(
			localDataSource = get(),
			mappers = get()
		)
	}
	
	factory<IMaintenanceRepository> {
		MaintenanceRepositoryImpl(
			localDataSource = get(),
			serverDataSource = get(),
			mappers = get()
		)
	}
	
	factory<IVehicleRepository> {
		VehicleRepositoryImpl(
			localDataSource = get(),
			serverDataSource = get(),
			localVinDecoder = get(),
			remoteVinDecoder = get(),
			mappers = get()
		)
	}
	
	factory<IFuelPricesRepository> {
		FuelPricesRepositoryImpl(
			localDataSource = get(),
			apiDataSource = get(),
			mappers = FuelPriceMappersFacade()
		)
	}
	factory<IFuelHistoryRepository> {
		FuelHistoryRepositoryImpl(
			localDataSource = get(),
			serverDataSource = get(),
			mappers = get()
		)
	}
	
	factory<ISettingsRepository> {
		SettingsRepositoryImpl(
			authDataSource = get(),
			dataDownloader = get()
		)
	}

}

