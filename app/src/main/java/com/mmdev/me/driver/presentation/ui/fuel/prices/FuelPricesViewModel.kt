/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel.FuelViewState.Error
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel.FuelViewState.Loading
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel.FuelViewState.Success
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * ViewModel used to connect between [IFuelPricesRepository] and UI
 */

class FuelPricesViewModel (private val repository: IFuelPricesRepository): BaseViewModel() {
	
	val fuelPrices : MutableLiveData<FuelViewState> = MutableLiveData()
	
	sealed class FuelViewState: ViewState {
		object Loading : FuelViewState()
		data class Success(val data: List<FuelStationWithPrices>) : FuelViewState()
		data class Error(val errorMessage: String) : FuelViewState()
	}
	
	fun getFuelPrices() {
		if (fuelPrices.value != null)
			return
		
		viewModelScope.launch {
			
			fuelPrices.postValue(Loading)
			
			withTimeout(30000) {
				
				repository.getFuelProvidersWithPrices().fold(
					success = {  fuelPrices.postValue(Success(data = it)) },
					failure = { fuelPrices.postValue(Error(it.localizedMessage!!)) }
				)
			}
		}
	}
	
}