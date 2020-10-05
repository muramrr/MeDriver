/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.user.UserData
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 *
 */

class VehicleViewModel (private val repository: IVehicleRepository) : BaseViewModel() {
	
	//init current vehicle from static inside Application class
	val chosenVehicle: MutableLiveData<Vehicle?> = MutableLiveData(MedriverApp.currentVehicle)
	
	//init vehicle list
	val vehicleList: MutableLiveData<List<Vehicle>> = MutableLiveData()
	init {
		viewModelScope.launch {
			repository.getAllSavedVehicles(MedriverApp.currentUser).collect { result ->
				result.fold(
					success = { vehicleList.postValue(it) },
					failure = { logError(TAG, "${it.message}") }
				)
			}
		}
	}
	
	// Bottom sheet dialog input fields
	val vinCodeInput: MutableLiveData<String?> = MutableLiveData()
	var isVinCodeReady = false
		set(value) {
			field = value
			checkIsAllFieldsBottomSheetFilledUp()
		}
	
	val brandInput: MutableLiveData<String?> = MutableLiveData()
	var isBrandReady = false
		set(value) {
			field = value
			checkIsAllFieldsBottomSheetFilledUp()
		}
	
	val modelInput: MutableLiveData<String?> = MutableLiveData()
	var isModelReady = false
		set(value) {
			field = value
			checkIsAllFieldsBottomSheetFilledUp()
		}
	
	val yearInput: MutableLiveData<String?> = MutableLiveData()
	var isYearReady = false
		set(value) {
			field = value
			checkIsAllFieldsBottomSheetFilledUp()
		}
	
	val odometerInput: MutableLiveData<String?> = MutableLiveData()
	var isOdometerReady = false
		set(value) {
			field = value
			checkIsAllFieldsBottomSheetFilledUp()
		}
	
	val engineCapacityInput: MutableLiveData<String?> = MutableLiveData()
	var isEngineCapReady = false
		set(value) {
			field = value
			checkIsAllFieldsBottomSheetFilledUp()
		}
	
	val isFormFilledCorrect: MutableLiveData<Boolean> = MutableLiveData(false)
	
	//check null or empty or vehicle with same vin doesn't exists
	fun checkAndAdd(user: UserData?) {
		with(buildVehicle()) {
			if (vehicleList.value.isNullOrEmpty() || !vehicleList.value!!.any { it.vin == this.vin }) {
				addVehicle(user, this)
			}
		}
		
	}
	
	private fun addVehicle(user: UserData?, vehicle: Vehicle) {
		viewModelScope.launch {
			repository.addVehicle(user, vehicle).collect { result ->
				result.fold(
					success = {
						logInfo(TAG, "Vehicle added.")
						
						//get new saved vehicles list
						repository.getAllSavedVehicles(user).collect { updatedList ->
							updatedList.fold(
								success = { updatedVehicleList ->
									
									//update list with newly added vehicle
									vehicleList.postValue(updatedVehicleList)
									
									//update current added vehicle only after new list was retrieved
									//this is guarantee that new vehicle was successfully added
									chosenVehicle.postValue(vehicle)
								},
								failure = { logError(TAG, "$it") }
							)
						}
					},
					failure = { logError(TAG, "$it") }
				)
			}
			//clear all fields after add executed no matter success or failure
			clearBottomSheetInput()
		}
	}
	
	//viewModel.getVehicleByVIN("WF0FXXWPDF3K73412")
	
	fun getVehicleByVIN(vinCode: String) {

		viewModelScope.launch {

			withTimeout(30000) {

				repository.getVehicleInfoByVin(vinCode).fold(
					success = {
						logInfo(TAG, "Found by VIN: $it")
						
						//autocomplete ui
						brandInput.postValue(it.brand)
						modelInput.postValue(it.model)
						yearInput.postValue(it.year.toString())
						engineCapacityInput.postValue(it.engineCapacity.toString())
						odometerInput.postValue("")
					},
					failure = { logError(TAG, "${it.message}") }
				)
			}
		}

	}
	
	private fun buildOdometerBound(): DistanceBound =
		when (MedriverApp.metricSystem) {
			MetricSystem.KILOMETERS -> DistanceBound(
				kilometers = odometerInput.value!!.toInt(),
				miles = null
			)
			MetricSystem.MILES -> DistanceBound(
				kilometers = null,
				miles = odometerInput.value!!.toInt()
			)
		}
	
	private fun buildVehicle(): Vehicle =
		Vehicle(
			brand = brandInput.value!!,
			model = modelInput.value!!,
			year = yearInput.value!!.toInt(),
			vin = vinCodeInput.value!!,
			odometerValueBound = buildOdometerBound(),
			engineCapacity = engineCapacityInput.value!!.toDouble()
		)
	
	
	private fun checkIsAllFieldsBottomSheetFilledUp() {
		if (isVinCodeReady && isBrandReady && isModelReady && isYearReady && isEngineCapReady &&
				isOdometerReady) isFormFilledCorrect.postValue(true)
		else isFormFilledCorrect.postValue(false)
	}
	
	private fun clearBottomSheetInput() {
		vinCodeInput.value = null
		brandInput.value = null
		modelInput.value = null
		yearInput.value = null
		odometerInput.value = null
		engineCapacityInput.value = null
	}
	
}