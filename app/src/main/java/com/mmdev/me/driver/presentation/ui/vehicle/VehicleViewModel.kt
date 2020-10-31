/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.10.2020 18:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 *
 */

class VehicleViewModel (private val repository: IVehicleRepository) : BaseViewModel() {
	
	//called when new entry was added
	val shouldBeUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
	
	//init current vehicle from static inside Application class
	val chosenVehicle: MutableLiveData<Vehicle?> = MutableLiveData(MedriverApp.currentVehicle)
	
	//init vehicle list
	val vehicleList: MutableLiveData<List<Vehicle>> = MutableLiveData(emptyList())
	init { getSavedVehicles() }
	
	
	
	fun getSavedVehicles() {
		viewModelScope.launch {
			repository.getAllSavedVehicles().fold(
				success = { vehicleList.postValue(it) },
				failure = { logError(TAG, "${it.message}") }
			)
			
		}
	}
	
	fun mapToUi(input: List<Vehicle>): List<VehicleUi> {
		return input.map { vehicle ->
			VehicleUi(
				icon = VehicleConstants.vehicleBrandIconMap.get(
					VehicleConstants.vehicleBrandIconMap.keys.find { possibleNames ->
						possibleNames.any { it.equals(vehicle.brand, true) }
					}
				) ?: 0,
				title = "${vehicle.brand} ${vehicle.model} (${vehicle.year}), ${vehicle.engineCapacity}",
				vin = vehicle.vin
			)
		}.plus(VehicleUi(R.drawable.ic_plus_in_frame_24, "Add new vehicle", ""))
	}
}