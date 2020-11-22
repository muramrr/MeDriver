/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 16:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.child.Child
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeUi
import com.mmdev.me.driver.presentation.utils.extensions.domain.buildDistanceBound
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

/**
 *
 */

class MaintenanceAddViewModel(private val repository: IMaintenanceRepository) : BaseViewModel() {
	
	val viewStateMap: HashMap<Int, MutableLiveData<MaintenanceAddViewState>> = hashMapOf()
	val parentShouldBeUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
	
	val selectedParentNode: MutableLiveData<ParentNodeUi?> = MutableLiveData()
	val selectedVehicleSystemNode: MutableLiveData<VehicleSystemNodeType?> = MutableLiveData()
	val selectedChildren: MutableLiveData<List<Child>?> = MutableLiveData()
	
	val multiSelectState: MutableLiveData<Boolean> = MutableLiveData(false)
	
//	val isSameDateForAll: MutableLiveData<Boolean> = MutableLiveData(false)
//	val isSameOdometerForAll: MutableLiveData<Boolean> = MutableLiveData(false)
	
	fun selectParentNode(parent: VehicleSystemNodeType, parentUi: ParentNodeUi) {
		selectedVehicleSystemNode.postValue(parent)
		selectedParentNode.postValue(parentUi)
	}
	
	
	
	val lastReplacedChildren: HashMap<SparePart, VehicleSparePart?> = hashMapOf()
	
	fun loadLastTimeSparePartReplaced(vin: String, list: List<Child>) {
		viewModelScope.launch(MyDispatchers.io()) {
			
			list.forEach { child ->
				if (child.sparePart.getSparePartName() != SparePart.OTHER)
					repository.findLastReplaced(
						vin,
						selectedVehicleSystemNode.value!!.toString(),
						child.sparePart.getSparePartName()
					).fold(
						success = {
							logWtf(TAG, "last replace = $it")
							logWtf(TAG, "searched = ${child.sparePart.getSparePartName()}")
							lastReplacedChildren.put(child.sparePart, it)
						},
						failure = {
							logError(TAG, "$it")
							lastReplacedChildren[child.sparePart] = null
						}
					)
			}
		}
		
	}
	
	
	fun addMaintenanceEntry(
		position: Int,
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
			
			val odometerBound = buildDistanceBound(odometerInput)
			
			repository.addMaintenanceItems(
				user,
				listOf(
					buildInputtedVehicleSparePart(
						dateInput,
						vendorInput,
						articulusInput,
						componentSelected,
						searchCriteria,
						commentaryInput,
						priceInput,
						odometerBound,
						vin
					)
				)
			).collect { result ->
				result.fold(
					success = {
						viewStateMap[position]!!.postValue(MaintenanceAddViewState.Success(odometerBound))
						parentShouldBeUpdated.postValue(true)
					},
					failure = {
						viewStateMap[position]!!.postValue(
							MaintenanceAddViewState.Error(it.localizedMessage)
						)
					}
				)
			}
		}
	}
	
	private fun buildInputtedVehicleSparePart(
		dateInput: LocalDateTime,
		vendorInput: String,
		articulusInput: String,
		componentSelected: SparePart,
		searchCriteria: List<String>,
		commentaryInput: String,
		priceInput: Double,
		odometerBound: DistanceBound,
		vin: String
	): VehicleSparePart = VehicleSparePart(
		date = dateInput,
		dateAdded = currentEpochTime(),
		articulus = articulusInput,
		vendor = vendorInput,
		systemNode = selectedVehicleSystemNode.value!!,
		systemNodeComponent = componentSelected,
		searchCriteria = searchCriteria,
		commentary = commentaryInput,
		moneySpent = priceInput,
		odometerValueBound = odometerBound,
		vehicleVinCode = vin
	)
	
//	fun addMaintenanceHistoryItems(list: List<VehicleSparePart>) {
//		viewModelScope.launch(MyDispatchers.io()) {
//			repository.addMaintenanceItems(MedriverApp.currentUser, list)
//		}
//	}
}