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

package com.mmdev.me.driver.presentation.ui.vehicle.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.vehicle.data.VehicleUi
import com.mmdev.me.driver.presentation.utils.extensions.domain.buildDistanceBound
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 *
 */

class VehicleAddViewModel(private val repository: IVehicleRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<VehicleAddViewState> = MutableLiveData()
	
	val vinCodeInput = MutableLiveData<String?>()
	val brandInput = MutableLiveData<String?>()
	val modelInput = MutableLiveData<String?>()
	val yearInput = MutableLiveData<String?>()
	val odometerInput = MutableLiveData<String?>()
	val engineCapacityInput = MutableLiveData<String?>()
	
	//viewModel.getVehicleByVIN("WF0FXXWPDF3K73412")
	
	
	// check null or empty vehicle list (no vehicles have been added yet)
	// or vehicle with same vin doesn't exists
	fun checkAndAdd(user: UserDataInfo?, vehicles: List<VehicleUi>?) {
		with(buildVehicle()) {
			if (vehicles?.any { it.vin == this.vin } == false) {
				addVehicle(user, this)
			}
			else viewState.postValue(VehicleAddViewState.ErrorSameVehicle(R.string.btm_sheet_vehicle_add_error_same_vin))
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
						if (brandInput.value.isNullOrBlank()) brandInput.postValue(it.brand)
						if (modelInput.value.isNullOrBlank()) modelInput.postValue(it.model)
						if (yearInput.value.isNullOrBlank()) yearInput.postValue(it.year.toString())
						if (engineCapacityInput.value.isNullOrBlank())
							engineCapacityInput.postValue(it.engineCapacity.toString())
						if (odometerInput.value.isNullOrBlank()) odometerInput.postValue("")
					},
					failure = {
						viewState.postValue(VehicleAddViewState.Error(it.localizedMessage))
					}
				)
			}
		}
	}
}