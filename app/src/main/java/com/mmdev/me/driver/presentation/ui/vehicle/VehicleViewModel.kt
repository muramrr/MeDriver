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
import com.mmdev.me.driver.domain.vehicle.data.Regulation
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
	val replacements = MutableLiveData<Map<SparePart, PendingReplacement?>?>()
	val expenses = MutableLiveData(Expenses())
	
	init {
		getSavedVehicles()
		getReplacementsList(chosenVehicle.value)
		getExpenses(chosenVehicle.value)
	}
	
	
	fun getSavedVehicles() {
		viewModelScope.launch {
			repository.getAllSavedVehicles().fold(
				success = {
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
				chosenVehicle.value?.maintenanceRegulations?.get(it.key) ?: Regulation()
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