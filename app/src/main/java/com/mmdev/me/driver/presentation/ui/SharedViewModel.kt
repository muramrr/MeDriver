/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 19:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.user.auth.AuthStatus
import com.mmdev.me.driver.domain.user.auth.IAuthFlowProvider
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.common.LoadingStatus
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewState
import com.mmdev.me.driver.presentation.ui.settings.AuthViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * [SharedViewModel] used in every fragment in app. Parent is MainActivity
 * Main responsibilities: Handle LOADING dialog appearance
 * Handle user [AuthStatus]
 * [currentVehicle] mutable value shared across all application
 */

class SharedViewModel(
	authProvider: IAuthFlowProvider, private val repository: IVehicleRepository // <-- is this bad?
) : BaseViewModel() {
	
	/**
	 * is this bad?
	 * used across all application, initializing in onCreate (MainActivity)
	 */
	val currentVehicle: MutableLiveData<Vehicle?> = MutableLiveData(null)
	init {
		viewModelScope.launch {
			currentVehicle.postValue(repository.getSavedVehicle(MedriverApp.currentVehicleVinCode))
		}
	}
	
	val showLoading: MutableLiveData<LoadingStatus> = MutableLiveData(LoadingStatus.HIDE)
	
	fun handleLoading(state: ViewState) {
		when (state) {
			is FuelHistoryViewState.Loading -> showLoading.value = LoadingStatus.SHOW
			is FuelPricesViewState.Loading -> showLoading.value = LoadingStatus.SHOW
			is AuthViewState.Loading -> showLoading.value = LoadingStatus.SHOW
			else -> showLoading.value = LoadingStatus.HIDE
		}
	}
	
	fun updateVehicle(user: UserModel?, vehicle: Vehicle) {
		viewModelScope.launch {
			repository.addVehicle(user, vehicle).collect { result ->
				result.fold(
					success = { currentVehicle.postValue(vehicle) },
					failure = { logError(TAG, "$it")}
				)
			}
		}
	}
	
	val userModel: LiveData<UserModel?> = authProvider.getAuthUserFlow().asLiveData()
	
}