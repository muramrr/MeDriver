/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.08.2020 15:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui

import androidx.lifecycle.MutableLiveData
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.common.LoadingState
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewState

/**
 * This is the documentation block about the class
 */

class SharedViewModel : BaseViewModel() {
	
	val showLoading: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.HIDE)
	
	fun handleLoading(state: ViewState) {
		when (state) {
			is FuelHistoryViewState.Loading -> showLoading.value = LoadingState.SHOW
			is FuelPricesViewState.Loading -> showLoading.value = LoadingState.SHOW
			else -> showLoading.value = LoadingState.HIDE
		}
		
	}
	
	
	
}