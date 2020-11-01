/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.11.2020 16:40
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
	private val vehicleList: MutableLiveData<List<Vehicle>> = MutableLiveData(emptyList())
	val vehicleUiList: MutableLiveData<List<VehicleUi>> = MutableLiveData(emptyList())
	
	init {
		getSavedVehicles()
	}
	
	
	fun getSavedVehicles() {
		viewModelScope.launch {
			repository.getAllSavedVehicles().fold(success = {
				vehicleList.postValue(it)
				vehicleUiList.postValue(mapToUi(it))
			}, failure = { logError(TAG, "${it.message}") })
			
		}
	}
	
	private fun mapToUi(input: List<Vehicle>): List<VehicleUi> {
		return input.map { vehicle ->
			VehicleUi(icon = VehicleConstants.vehicleBrandIconMap.getOrDefault(vehicle.brand, 0),
			          title = "${vehicle.brand} ${vehicle.model} (${vehicle.year}), ${vehicle.engineCapacity}",
			          vin = vehicle.vin)
		}.plus(VehicleUi(R.drawable.ic_plus_in_frame_24, "", R.string.fg_vehicle_add_new_vehicle, ""))
	}
	
	fun setVehicle(position: Int) {
		if (position > vehicleList.value!!.size) return
		chosenVehicle.postValue(vehicleList.value!![position])
	}
	
}