/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.10.2020 18:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.presentation.ui.SharedViewModel
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewModel
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.ui.home.HomeViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewModel
import com.mmdev.me.driver.presentation.ui.settings.SettingsViewModel
import com.mmdev.me.driver.presentation.ui.vehicle.VehicleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * [ViewModelsModule] provides ViewModel instances
 */

val ViewModelsModule = module {


	viewModel { HomeViewModel() }
	viewModel { MaintenanceViewModel(repository = get()) }
	viewModel { MaintenanceAddViewModel(repository = get()) }
	viewModel { VehicleViewModel(repository = get()) }
	viewModel { FuelPricesViewModel(repository = get()) }
	viewModel { FuelHistoryViewModel(repository = get()) }
	viewModel { SettingsViewModel(repository = get()) }

	viewModel { SharedViewModel(authProvider = get(), fetcher = get()) }

}