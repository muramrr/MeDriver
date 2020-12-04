/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 18:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants
import kotlinx.coroutines.launch

/**
 *
 */

class VehicleViewModel (private val repository: IVehicleRepository) : BaseViewModel() {
	
	//called when new entry was added
	val shouldBeUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
	
	//init current vehicle from static inside Application class
	val chosenVehicle: MutableLiveData<Vehicle?> = MutableLiveData(MainActivity.currentVehicle)
	
	//init vehicle list
	private val vehicleList: MutableLiveData<List<Vehicle>> = MutableLiveData(emptyList())
	val vehicleUiList: MutableLiveData<List<VehicleUi>> = MutableLiveData(emptyList())
	val replacements: MutableLiveData<Map<SparePart, PendingReplacement?>?> = MutableLiveData()
	val expenses: MutableLiveData<Expenses> = MutableLiveData()
	
	init {
		getSavedVehicles()
		getReplacementsList(chosenVehicle.value)
		getExpenses(chosenVehicle.value)
	}
	
	
	fun getSavedVehicles() {
		viewModelScope.launch {
			repository.getAllSavedVehicles().fold(
				success = {
					if (!it.contains(MainActivity.currentVehicle)) chosenVehicle.value = null
					vehicleList.postValue(it)
					vehicleUiList.postValue(mapVehicle(it))
				},
				failure = { logError(TAG, "${it.message}") }
			)
			
		}
	}
	
	private fun mapVehicle(input: List<Vehicle>): List<VehicleUi> {
		return input.map { vehicle ->
			VehicleUi(
				icon = VehicleConstants.vehicleBrandIconMap.getOrDefault(vehicle.brand, 0),
				title = "${vehicle.brand} ${vehicle.model} (${vehicle.year}), ${vehicle.engineCapacity}",
				vin = vehicle.vin
			)
		}.plus(VehicleUi(R.drawable.ic_plus_in_frame_24, "", R.string.fg_vehicle_add_new_vehicle, ""))
	}
	
	fun setVehicle(position: Int) {
		if (position > vehicleList.value!!.size) return
		with(vehicleList.value!![position]) {
			chosenVehicle.postValue(this)
			getReplacementsList(this)
			getExpenses(this)
		}
	}
	
	
	private fun getReplacementsList(vehicle: Vehicle?) {
		if (vehicle == null) replacements.value = null
		else {
			viewModelScope.launch {
				repository.getPendingReplacements(vehicle).fold(
					success = { replacements.value = it },
					failure = { logError(TAG, "${it.message}") }
				)
			}
		}
	}
	
	fun buildConsumables(replacements: Map<SparePart, PendingReplacement?>?): List<ConsumablePartUi> =
		replacements?.map {
			ConsumablePartUi(
				VehicleSystemNodeConstants.plannedComponents[replacements.keys.indexOf(it.key)],
				it.value,
				chosenVehicle.value!!.maintenanceRegulations[it.key]
			)
		} ?: List(PlannedParts.valuesArray.size) {
			ConsumablePartUi(VehicleSystemNodeConstants.plannedComponents[it])
		}
	
	
	private fun getExpenses(vehicle: Vehicle?) {
		if (vehicle == null) replacements.value = null
		else {
			viewModelScope.launch {
				repository.getExpensesInfo(vehicle.vin).fold(
					success = { expenses.value = it },
					failure = { logError(TAG, "${it.message}") }
				)
			}
		}
	}
}