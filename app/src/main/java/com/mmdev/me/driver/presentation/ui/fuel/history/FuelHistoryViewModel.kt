/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.garage.DataGenerator
import kotlinx.coroutines.flow.collect
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
				failure = { viewState.postValue(FuelHistoryViewState.Error(it.localizedMessage)) }
			)
		}
	}
	
	fun loadNextHistory() {
		viewModelScope.launch {
			//viewState.postValue(Loading)
			
			repository.getMoreFuelHistory(MedriverApp.currentVehicleVinCode).fold(
				success = { viewState.postValue(FuelHistoryViewState.LoadNext(data = it)) },
				failure = { viewState.postValue(FuelHistoryViewState.Error(it.localizedMessage)) }
			)
		}
	}
	
	fun loadPreviousHistory() {
		viewModelScope.launch {
			//viewState.postValue(Loading)
			
			repository.getPreviousFuelHistory(MedriverApp.currentVehicleVinCode).fold(
				success = { viewState.postValue(FuelHistoryViewState.LoadPrevious(data = it)) },
				failure = { viewState.postValue(FuelHistoryViewState.Error(it.localizedMessage)) }
			)
		}
	}
	
	fun delete(entry: FuelHistory, position: Int) {
		viewModelScope.launch {
			repository.removeFuelHistoryRecord(MainActivity.currentUser, entry).collect { result ->
				result.fold(
					success = {
						viewState.postValue(FuelHistoryViewState.Delete(position))
					},
					failure = {
						viewState.postValue(FuelHistoryViewState.Error(it.localizedMessage))
					}
				)
			}
		}
	}

	fun generateFuelHistoryData() {
		viewModelScope.launch {
			DataGenerator.fastGenerateFuelHistory(MainActivity.currentVehicle!!.vin)
		}
	}
}