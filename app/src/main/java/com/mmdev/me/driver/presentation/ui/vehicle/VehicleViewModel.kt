/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 20:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *
 */

class VehicleViewModel (private val repository: IVehicleRepository) : BaseViewModel() {
	
	val chosenVehicle: MutableLiveData<Vehicle?> = MutableLiveData(MedriverApp.currentVehicle)
	val vehicleList: MutableLiveData<List<Vehicle>> = MutableLiveData()
	init {
		if (MedriverApp.currentUser != null && MedriverApp.currentUser!!.isPremium)
			viewModelScope.launch {
				repository.getAllVehicles(MedriverApp.currentUser!!).collect { result ->
					result.fold(
						success = { updatedVehicleList ->
							vehicleList.postValue(updatedVehicleList)
						},
						failure = { logError(TAG, "${it.message}") }
					)
				}
			}
		else vehicleList.postValue(emptyList())
	}
	
	//check null or empty or vehicle with same vin doesn't exists
	fun checkAndAdd(user: UserModel, vehicle: Vehicle) {
		if (vehicleList.value.isNullOrEmpty() || !vehicleList.value!!.any { it.vin == vehicle.vin }) {
			addVehicle(user, vehicle)
		}
	}
	
	private fun addVehicle(user: UserModel, vehicle: Vehicle) {
		viewModelScope.launch {
			repository.addVehicle(user, vehicle).collect { result ->
				result.fold(
					success = {
						repository.getAllVehicles(user).collect { updatedList ->
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
}