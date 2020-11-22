/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 16:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.extensions.currentTimeAndDate
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.*
import com.mmdev.me.driver.presentation.utils.extensions.combineWith
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

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
		if (position != 0) getHistoryBySystemNode(VehicleSystemNodeType.valuesArray[position - 1])
		else loadInitMaintenanceHistory()
	}
	
	private fun getHistoryBySystemNode(node: VehicleSystemNodeType) {
		viewModelScope.launch {
			viewState.postValue(Loading)
			
			repository.getSystemNodeHistory(MedriverApp.currentVehicleVinCode, node.name).fold(
				success = { data ->
					viewState.postValue(Filter(data = data))
				},
				failure = {
					viewState.postValue(Error(it.localizedMessage!!))
				}
			)
		}
	}
	
	
	fun searchMaintenanceHistory(query: String) {
		viewModelScope.launch {
			
			delay(1000)
			viewState.postValue(Loading)
			
			repository.getHistoryByTypedQuery(MedriverApp.currentVehicleVinCode, query).fold(
				success = { viewState.postValue(Init(it)) },
				failure = { viewState.postValue(Error(it.localizedMessage)) }
			)
		}
	}

	fun addRandomEntries() {
		viewModelScope.launch(MyDispatchers.io()) {
			VehicleSystemNodeType.valuesArray.forEach { parent ->
				parent.getChildren().forEach { child ->
					repository.addMaintenanceItems(
						MedriverApp.currentUser,
						listOf(
							VehicleSparePart(
								date = currentTimeAndDate(),
								dateAdded = currentEpochTime(),
								articulus = "",
								vendor = "",
								systemNode = parent,
								systemNodeComponent = child,
								searchCriteria = listOf(child.getSparePartName()),
								commentary = "",
								Random.nextInt(100, 9999).toDouble(),
								MedriverApp.currentVehicle!!.odometerValueBound,
								MedriverApp.currentVehicleVinCode
							)
						)
					).collect { result ->
						result.fold(
							success = {
							
							},
							failure = {
							
							}
						)
					}
					
				}
				
			}
			
		}
	}
	
}