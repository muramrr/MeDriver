/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.11.2020 19:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.domain.fuel.prices.data.Region
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.todayAt

/**
 * ViewModel used to connect between [IFuelPricesRepository] and UI
 */

class FuelPricesViewModel (private val repository: IFuelPricesRepository): BaseViewModel() {
	
	val viewState : MutableLiveData<FuelPricesViewState> = MutableLiveData()
	val fuelPrices: MutableLiveData<List<FuelStationWithPrices>> = MutableLiveData()
	
	init { getFuelPrices(MedriverApp.pricesRegion) }
	
	fun getFuelPrices(region: Region) {
		
		val localDate = Clock.System.todayAt(currentSystemDefault()).toString()
		
		viewModelScope.launch {
			
			viewState.postValue(FuelPricesViewState.Loading)
			
			withTimeout(30000) {
				delay(500)
				
				repository.getFuelStationsWithPrices(localDate, region).fold(
					success = {
						MedriverApp.pricesRegion = region
						viewState.postValue(FuelPricesViewState.Success(data = it))
						fuelPrices.value = it
					},
					failure = {
						viewState.postValue(FuelPricesViewState.Error(it.localizedMessage))
					}
				)
			}
		}
	}
	
}