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

package com.mmdev.me.driver.presentation.ui.maintenance


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.garage.DataGenerator
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.*
import com.mmdev.me.driver.presentation.utils.extensions.combineWith
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *
 */

class MaintenanceViewModel (private val repository: IMaintenanceRepository) : BaseViewModel() {
	
	val viewState: MutableLiveData<MaintenanceHistoryViewState> = MutableLiveData()
	
	//indicates is history empty or not
	val isHistoryEmpty = MutableLiveData<Boolean>()
	val isAddDialogShowing = MutableLiveData(false)
	val shouldUpdateData = MutableLiveData(false)
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
				failure = { viewState.postValue(Error(it.localizedMessage)) }
			)
		}
	}
	
	fun loadNextMaintenanceHistory() {
		viewModelScope.launch {
			
			repository.getMoreMaintenanceHistory(MedriverApp.currentVehicleVinCode).fold(
				success = { viewState.postValue(LoadNext(data = it)) },
				failure = { viewState.postValue(Error(it.localizedMessage)) }
			)
			
		}
	}
	
	fun loadPreviousMaintenanceHistory() {
		viewModelScope.launch {
			
			repository.getPreviousMaintenanceHistory(MedriverApp.currentVehicleVinCode).fold(
				success = { viewState.postValue(LoadPrevious(data = it)) },
				failure = { viewState.postValue(Error(it.localizedMessage)) }
			)
			
		}
	}
	
	fun delete(entry: VehicleSparePart, position: Int) {
		viewModelScope.launch {
			repository.removeMaintenanceEntry(MainActivity.currentUser, entry).collect { result ->
				result.fold(
					success = {
						viewState.postValue(Delete(position))
					},
					failure = {
						viewState.postValue(Error(it.localizedMessage))
					}
				)
			}
		}
	}
	
	
	fun filterHistory(position: Int) {
		if (position != 8) getHistoryBySystemNode(VehicleSystemNodeType.valuesArray[position])
		else loadInitMaintenanceHistory()
	}
	
	private fun getHistoryBySystemNode(node: VehicleSystemNodeType) {
		viewModelScope.launch {
			viewState.postValue(Loading)
			
			repository.getSystemNodeHistory(MedriverApp.currentVehicleVinCode, node).fold(
				success = { data ->
					viewState.postValue(Filter(data = data))
					isHistoryEmpty.value = data.isEmpty()
				},
				failure = { viewState.postValue(Error(it.localizedMessage)) }
			)
		}
	}
	
	
	fun searchMaintenanceHistory(query: String) {
		logDebug(TAG, "Performing search")
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
	
	fun generateMaintenanceData(context: Context) {
		viewModelScope.launch {
			DataGenerator.fastGenerateMaintenance(context, MainActivity.currentVehicle!!.vin)
		}
	}
	
}