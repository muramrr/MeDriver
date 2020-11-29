/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.11.2020 02:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.Error
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.Filter
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.Init
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.LoadNext
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.LoadPrevious
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.Loading
import com.mmdev.me.driver.presentation.utils.extensions.combineWith
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 */

class MaintenanceViewModel (private val repository: IMaintenanceRepository) : BaseViewModel() {
	
	val viewState: MutableLiveData<MaintenanceHistoryViewState> = MutableLiveData()
	
	//indicates is history empty or not
	val isHistoryEmpty: MutableLiveData<Boolean> = MutableLiveData()
	val isAddDialogShowing: MutableLiveData<Boolean> = MutableLiveData(false)
	val shouldUpdateData: MutableLiveData<Boolean> = MutableLiveData(false)
	val updateTrigger = isAddDialogShowing.combineWith(shouldUpdateData) { dialog, update ->
		if (dialog != null && update != null) {
			if (!dialog && update) loadInitMaintenanceHistory()
		}
	}
	
	init { loadInitMaintenanceHistory() }
	
	private fun loadInitMaintenanceHistory() {
		viewModelScope.launch {
			viewState.postValue(Loading)
			
			repository.getInitMaintenanceHistory(MedriverApp.currentVehicleVinCode).fold(
				success = {
					//in case below no matter if list is empty or not
					viewState.postValue(Init(data = it))
					
					//handle empty state visibility
					isHistoryEmpty.value = it.isEmpty()
				},
				failure = { viewState.postValue(Error(it.localizedMessage!!)) }
			)
		}
	}
	
	fun loadNextMaintenanceHistory() {
		viewModelScope.launch {
			//viewState.postValue(Loading)
			
			repository.getMoreMaintenanceHistory(MedriverApp.currentVehicleVinCode).fold(
				success = { viewState.postValue(LoadNext(data = it)) },
				failure = { viewState.postValue(Error(it.localizedMessage!!)) }
			)
		}
	}
	
	fun loadPreviousMaintenanceHistory() {
		viewModelScope.launch {
			//viewState.postValue(Loading)
			
			repository.getPreviousMaintenanceHistory(MedriverApp.currentVehicleVinCode).fold(
				success = { viewState.postValue(LoadPrevious(data = it)) },
				failure = { viewState.postValue(Error(it.localizedMessage!!)) }
			)
		}
	}
	
	
	fun filterHistory(position: Int) {
		if (position != 8) getHistoryBySystemNode(VehicleSystemNodeType.valuesArray[position])
		else loadInitMaintenanceHistory()
	}
	
	private fun getHistoryBySystemNode(node: VehicleSystemNodeType) {
		viewModelScope.launch {
			viewState.postValue(Loading)
			
			repository.getSystemNodeHistory(MedriverApp.currentVehicleVinCode, node.name).fold(
				success = { data ->
					viewState.postValue(Filter(data = data))
					isHistoryEmpty.value = data.isEmpty()
				},
				failure = {
					viewState.postValue(Error(it.localizedMessage!!))
				}
			)
		}
	}
	
	
	fun searchMaintenanceHistory(query: String) {
		logWtf(TAG, "Performing search")
		viewModelScope.launch {
			
			delay(1000)
			
			viewState.postValue(Loading)
			
			//if user clicks "search ime" on blank or empty input field
			if (query.isBlank()) loadInitMaintenanceHistory()
			else repository.getHistoryByTypedQuery(MedriverApp.currentVehicleVinCode, query).fold(
				success = {
					viewState.postValue(Init(it))
					isHistoryEmpty.value = it.isEmpty()
				},
				failure = { viewState.postValue(Error(it.localizedMessage)) }
			)
		}
	}
	
}