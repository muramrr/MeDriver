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

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.utils.extensions.domain.buildDistanceBound
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

/**
 *
 */

class ChildEditViewModel(private val repository: IMaintenanceRepository): BaseViewModel() {
	
	val lastReplacedChild = MutableLiveData<VehicleSparePart?>()
	val viewState = MutableLiveData<ChildEditViewState>()
	
	fun loadLastTimeSparePartReplaced(parent: VehicleSystemNodeType, child: Child) {
		if (child.sparePart.getSparePartName() != SparePart.OTHER) {
			
			viewModelScope.launch(MyDispatchers.io()) {
				
				repository.findLastReplaced(MedriverApp.currentVehicleVinCode, parent, child.sparePart).fold(
					success = { lastReplacedChild.postValue(it) },
					failure = {
						logError(TAG, "$it")
						lastReplacedChild.postValue(null)
					}
				)
			}
			logWtf(TAG, "searched = ${child.sparePart.getSparePartName()}")
		}
	}
	
	fun addMaintenanceEntry(
		parent: VehicleSystemNodeType,
		user: UserDataInfo?,
		dateInput: LocalDateTime,
		vendorInput: String,
		articulusInput: String,
		componentSelected: SparePart,
		searchCriteria: List<String>,
		commentaryInput: String,
		priceInput: Double,
		odometerInput: Int,
		vin: String
	) {
		viewModelScope.launch(MyDispatchers.io()) {
			
			val vehicleSparePart = VehicleSparePart(
				date = dateInput,
				dateAdded = currentEpochTime(),
				articulus = articulusInput,
				vendor = vendorInput,
				systemNode = parent,
				systemNodeComponent = componentSelected,
				searchCriteria = searchCriteria,
				commentary = commentaryInput,
				moneySpent = priceInput,
				odometerValueBound = buildDistanceBound(odometerInput),
				vehicleVinCode = vin
			)
			
			repository.addMaintenanceItems(
				user,
				listOf(vehicleSparePart)
			).collect { result ->
				result.fold(
					success = { viewState.postValue(ChildEditViewState.Success(vehicleSparePart)) },
					failure = { viewState.postValue(ChildEditViewState.Error(it.localizedMessage)) }
				)
			}
		}
	}
	
}