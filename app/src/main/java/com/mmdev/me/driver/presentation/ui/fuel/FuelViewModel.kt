/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 18:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.IFuelRepository
import com.mmdev.me.driver.domain.fuel.model.FuelPrice
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 */

class FuelViewModel (private val repository: IFuelRepository) : BaseViewModel() {
	
	val fuelInfo: MutableLiveData<FuelViewState> = MutableLiveData()
	
	sealed class FuelViewState: ViewState {
		object Loading : FuelViewState()
		data class Success(val data: List<FuelPrice>) : FuelViewState()
		data class Error(val errorMessage: String) : FuelViewState()
	}
	
	fun getFuelInfo(fuelType: FuelType) {
		val date = Calendar.getInstance().time
		val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		val formattedDate = formatter.format(date)
		viewModelScope.launch {
			
			fuelInfo.postValue(FuelViewState.Loading)
			
			withTimeout(30000) {
				
				repository.getFuelPrices(fuelType, formattedDate).fold(
					success = { fuelInfo.postValue(FuelViewState.Success(data = it)) },
					failure = { fuelInfo.postValue(FuelViewState.Error(it.localizedMessage!!)) }
				)
			}
		}
		
	}
	
}