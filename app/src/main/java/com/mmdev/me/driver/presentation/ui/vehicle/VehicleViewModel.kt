/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.09.2020 18:54
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
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
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
	val brandInput: MutableLiveData<String?> = MutableLiveData()
	val modelInput: MutableLiveData<String?> = MutableLiveData()
	val yearInput: MutableLiveData<String?> = MutableLiveData()
	val odometerInput: MutableLiveData<String?> = MutableLiveData()
	val engineCapacityInput: MutableLiveData<String?> = MutableLiveData()
	
	
	
	//check null or empty or vehicle with same vin doesn't exists
	fun checkAndAdd(user: UserModel) {
		with(buildVehicle()) {
			if (vehicleList.value.isNullOrEmpty() || !vehicleList.value!!.any { it.vin == this.vin }) {
				addVehicle(user, this)
			}
		}
		
	}
	
	private fun addVehicle(user: UserModel, vehicle: Vehicle) {
		viewModelScope.launch {
			repository.addVehicle(user, vehicle).collect { result ->
				result.fold(
					success = {
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
		}
	}
	
	//viewModel.getVehicleByVIN("WF0FXXWPDF3K73412")
	
	fun getVehicleByVIN(vinCode: String) {

		viewModelScope.launch {

			withTimeout(30000) {

				repository.getVehicleInfoByVin(vinCode).fold(
					success = {
						logInfo(TAG, "$it")
						
						//autocomplete ui
						brandInput.postValue(it.brand)
						modelInput.postValue(it.model)
						yearInput.postValue(it.year.toString())
						engineCapacityInput.postValue(it.engineCapacity.toString())
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

}