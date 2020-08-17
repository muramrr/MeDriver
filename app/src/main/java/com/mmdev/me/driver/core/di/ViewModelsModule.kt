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

import com.mmdev.me.driver.presentation.ui.SharedViewModel
import com.mmdev.me.driver.presentation.ui.care.CareViewModel
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewModel
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.ui.home.HomeViewModel
import com.mmdev.me.driver.presentation.ui.mycar.MyCarViewModel
import com.mmdev.me.driver.presentation.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * [ViewModelsModule] provides ViewModel instances
 */

val ViewModelsModule = module {


	viewModel { HomeViewModel(repository = get()) }
	viewModel { CareViewModel() }
	viewModel { MyCarViewModel() }
	viewModel { FuelPricesViewModel(repository = get()) }
	viewModel { FuelHistoryViewModel(repository = get()) }
	viewModel { SettingsViewModel() }

	viewModel { SharedViewModel() }

}