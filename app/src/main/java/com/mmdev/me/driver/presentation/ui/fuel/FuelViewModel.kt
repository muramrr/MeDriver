/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 18:28
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.data.datasource.remote.fuel.model.NetworkFuelModelResponse
import com.mmdev.me.driver.domain.core.RepositoryState
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.IFuelRepository
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 *
 */

class FuelViewModel (private val repository: IFuelRepository) : BaseViewModel() {
	
	val fuelTypesList: List<FuelType> = FuelType.values().toList()
	val fuelInfo: MutableLiveData<FuelViewState> = MutableLiveData()
	
	sealed class FuelViewState: ViewState {
		object Loading : FuelViewState()
		data class Success(val data: Map<FuelType, NetworkFuelModelResponse>) : FuelViewState()
		data class Error(val errorMessage: String) : FuelViewState()
	}
	
	fun getFuelInfo(date: String, region: Int = 3) {
		
		viewModelScope.launch {
			
			fuelInfo.postValue(FuelViewState.Loading)
			
			when (val result =
				withTimeout(30000) {
					repository.getFuelInfo(date, region)
				}) {
				
				is RepositoryState.Success ->
					fuelInfo.postValue(FuelViewState.Success(data = result.data))
				
				is RepositoryState.Error ->
					fuelInfo.postValue(FuelViewState.Error(result.errorMessage))
				
			}
		}
		
	}
	
}