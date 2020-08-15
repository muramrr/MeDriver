/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.08.20 19:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.domain.fuel.IFuelRepository
import com.mmdev.me.driver.domain.fuel.model.FuelStationWithPrices
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 *
 */

class FuelViewModel (private val repository: IFuelRepository): BaseViewModel() {
	
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
			
			fuelPrices.postValue(FuelViewState.Loading)
			
			withTimeout(30000) {
				
				repository.getFuelProvidersWithPrices().fold(
					success = {  fuelPrices.postValue(FuelViewState.Success(data = it)) },
					failure = { fuelPrices.postValue(FuelViewState.Error(it.localizedMessage!!)) }
				)
			}
		}
	}
	
}