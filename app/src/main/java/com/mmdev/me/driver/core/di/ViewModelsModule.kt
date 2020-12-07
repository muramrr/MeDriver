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

import com.mmdev.me.driver.presentation.ui.SharedViewModel
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewModel
import com.mmdev.me.driver.presentation.ui.fuel.history.add.FuelHistoryAddViewModel
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.ui.home.HomeViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.child.ChildEditViewModel
import com.mmdev.me.driver.presentation.ui.settings.SettingsViewModel
import com.mmdev.me.driver.presentation.ui.settings.auth.AuthViewModel
import com.mmdev.me.driver.presentation.ui.vehicle.VehicleViewModel
import com.mmdev.me.driver.presentation.ui.vehicle.add.VehicleAddViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * [ViewModelsModule] provides ViewModel instances
 */

val ViewModelsModule = module {
	
	viewModel { HomeViewModel(repository = get(), get(), get(), get()) }
	
	viewModel { MaintenanceViewModel(repository = get()) }
	viewModel { MaintenanceAddViewModel() }
	viewModel { ChildEditViewModel(repository = get()) }
	
	viewModel { VehicleViewModel(repository = get()) }
	viewModel { VehicleAddViewModel(repository = get()) }
	
	viewModel { FuelPricesViewModel(repository = get()) }
	
	viewModel { FuelHistoryViewModel(repository = get()) }
	viewModel { FuelHistoryAddViewModel(repository = get()) }
	
	viewModel { SettingsViewModel(repository = get()) }
	viewModel { AuthViewModel(repository = get()) }

	viewModel { SharedViewModel(authProvider = get(), fetcher = get()) }

}