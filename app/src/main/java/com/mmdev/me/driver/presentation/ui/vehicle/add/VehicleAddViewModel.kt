/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 18:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.utils.extensions.domain.buildDistanceBound
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 *
 */

class VehicleAddViewModel(private val repository: IVehicleRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<VehicleAddViewState> = MutableLiveData()
	
	val vinCodeInput: MutableLiveData<String?> = MutableLiveData()
	val brandInput: MutableLiveData<String?> = MutableLiveData()
	val modelInput: MutableLiveData<String?> = MutableLiveData()
	val yearInput: MutableLiveData<String?> = MutableLiveData()
	val odometerInput: MutableLiveData<String?> = MutableLiveData()
	val engineCapacityInput: MutableLiveData<String?> = MutableLiveData()
	
	//viewModel.getVehicleByVIN("WF0FXXWPDF3K73412")
	
	val vehicleList: MutableLiveData<List<Vehicle>> = MutableLiveData(emptyList())
	init {
		viewModelScope.launch {
			repository.getAllSavedVehicles().fold(
				success = { vehicleList.value = it },
				failure = { logError(TAG, "${it.message}") }
			)
			
		}
	}
	
	// check null or empty vehicle list (no vehicles have been added yet)
	// or vehicle with same vin doesn't exists
	fun checkAndAdd(user: UserDataInfo?) {
		with(buildVehicle()) {
			if (vehicleList.value.isNullOrEmpty() || !vehicleList.value!!.any { it.vin == this.vin }) {
				addVehicle(user, this)
			}
		}
		
	}
	
	private fun addVehicle(user: UserDataInfo?, vehicle: Vehicle) {
		viewModelScope.launch {
			repository.addVehicle(user, vehicle).collect { result ->
				result.fold(
					success = { viewState.postValue(VehicleAddViewState.Success) },
					failure = { viewState.postValue(VehicleAddViewState.Error(it.localizedMessage)) }
				)
			}
		}
	}
	
	private fun buildVehicle(): Vehicle =
		Vehicle(
			brand = brandInput.value!!,
			model = modelInput.value!!,
			year = yearInput.value!!.toInt(),
			vin = vinCodeInput.value!!,
			odometerValueBound = buildDistanceBound(odometerInput.value!!.toInt()),
			engineCapacity = engineCapacityInput.value!!.toDouble(),
			lastUpdatedDate = currentEpochTime()
		)
	
	
	fun getVehicleByVIN(vinCode: String) {
		
		viewState.postValue(VehicleAddViewState.Loading)
		
		viewModelScope.launch {
			
			withTimeout(30000) {
				
				repository.getVehicleInfoByVin(vinCode).fold(
					success = {
						viewState.postValue(VehicleAddViewState.Idle)
						
						logInfo(TAG, "Found by VIN: $it")
						
						//autocomplete ui
						brandInput.postValue(it.brand)
						modelInput.postValue(it.model)
						yearInput.postValue(it.year.toString())
						engineCapacityInput.postValue(it.engineCapacity.toString())
						odometerInput.postValue("")
					},
					failure = {
						viewState.postValue(VehicleAddViewState.Error(it.localizedMessage))
					}
				)
			}
		}
	}
}