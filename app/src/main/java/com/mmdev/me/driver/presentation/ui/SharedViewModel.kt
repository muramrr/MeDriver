/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 16:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.user.auth.AuthStatus
import com.mmdev.me.driver.domain.user.auth.IAuthFlowProvider
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.common.LoadingStatus
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewState
import com.mmdev.me.driver.presentation.ui.settings.AuthViewState

/**
 * [SharedViewModel] used in every fragment in app. Parent is MainActivity
 * Main responsibilities: Handle LOADING dialog appearance
 * Handle user [AuthStatus]
 */

class SharedViewModel(authProvider: IAuthFlowProvider) : BaseViewModel() {
	
	val showLoading: MutableLiveData<LoadingStatus> = MutableLiveData(LoadingStatus.HIDE)
	
	fun handleLoading(state: ViewState) {
		when (state) {
			is FuelHistoryViewState.Loading -> showLoading.value = LoadingStatus.SHOW
			is FuelPricesViewState.Loading -> showLoading.value = LoadingStatus.SHOW
			is AuthViewState.Loading -> showLoading.value = LoadingStatus.SHOW
			else -> showLoading.value = LoadingStatus.HIDE
		}
		
	}
	
	val authStatus: LiveData<AuthStatus> = authProvider.getAuthStatusFlow().asLiveData()
	val userModel: LiveData<UserModel?> = authProvider.getAuthUserFlow().asLiveData()
	
}