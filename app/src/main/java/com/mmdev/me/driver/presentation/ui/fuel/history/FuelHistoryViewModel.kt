/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.11.2020 19:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel for [FuelHistoryFragment]
 */

class FuelHistoryViewModel(private val repository: IFuelHistoryRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<FuelHistoryViewState> = MutableLiveData()
	
	//called when new entry was added
	val shouldBeUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
	
	//indicates is history empty or not
	val isHistoryEmpty: MutableLiveData<Boolean> = MutableLiveData()
	
	init { loadInitHistory() }
	
	fun loadInitHistory() {
		viewModelScope.launch {
			viewState.postValue(FuelHistoryViewState.Loading)
			
			repository.getInitFuelHistory(MedriverApp.currentVehicleVinCode).fold(
				success = {
					//in case below no matter if list is empty or not
					viewState.postValue(FuelHistoryViewState.Init(data = it))
					
					//handle empty state visibility
					isHistoryEmpty.value = it.isEmpty()
				},
				failure = { viewState.postValue(FuelHistoryViewState.Error(it.localizedMessage!!)) }
			)
		}
	}
	
	fun loadNextHistory() {
		viewModelScope.launch {
			//viewState.postValue(Loading)
			
			repository.getMoreFuelHistory(MedriverApp.currentVehicleVinCode).fold(
				success = { viewState.postValue(FuelHistoryViewState.LoadNext(data = it)) },
				failure = { viewState.postValue(FuelHistoryViewState.Error(it.localizedMessage!!)) }
			)
		}
	}
	
	fun loadPreviousHistory() {
		viewModelScope.launch {
			//viewState.postValue(Loading)
			
			repository.getPreviousFuelHistory(MedriverApp.currentVehicleVinCode).fold(
				success = { viewState.postValue(FuelHistoryViewState.LoadPrevious(data = it)) },
				failure = { viewState.postValue(FuelHistoryViewState.Error(it.localizedMessage!!)) }
			)
		}
	}
	
}