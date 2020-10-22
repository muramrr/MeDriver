/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.10.2020 20:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.Loading
import kotlinx.coroutines.launch

/**
 *
 */

class MaintenanceViewModel (private val repository: IMaintenanceRepository) : BaseViewModel() {
	
	val viewState: MutableLiveData<MaintenanceHistoryViewState> = MutableLiveData()
	
	//indicates is history empty or not
	val isHistoryEmpty: MutableLiveData<Boolean> = MutableLiveData()
	
	fun loadMaintenanceHistory(size: Int? = null) {
		viewModelScope.launch {
			viewState.postValue(Loading)
			
			repository.getMaintenanceHistory(MedriverApp.currentVehicleVinCode, size).fold(
				success = { data ->
					if (size == null) {
						//in case below no matter if list is empty or not
						viewState.postValue(MaintenanceHistoryViewState.Init(data = data))
						
						//handle empty state visibility
						isHistoryEmpty.value = data.isEmpty()
						
					} // if size was specified
					else viewState.postValue(MaintenanceHistoryViewState.Paginate(data = data))
					
					//loadedHistory.addAll(data)
				},
				failure = {
					viewState.postValue(MaintenanceHistoryViewState.Error(it.localizedMessage!!))
				}
			)
		}
	}
	
	
	fun filterHistory(position: Int) {
		if (position != 0) getHistoryBySystemNode(VehicleSystemNodeType.valuesArray[position - 1])
		else loadMaintenanceHistory()
	}
	
	private fun getHistoryBySystemNode(node: VehicleSystemNodeType) {
		viewModelScope.launch {
			viewState.postValue(Loading)
			
			repository.getSystemNodeHistory(MedriverApp.currentVehicleVinCode, node.name).fold(
				success = { data ->
					viewState.postValue(MaintenanceHistoryViewState.Filter(data = data))
				},
				failure = {
					viewState.postValue(MaintenanceHistoryViewState.Error(it.localizedMessage!!))
				}
			)
		}
	}


	
}