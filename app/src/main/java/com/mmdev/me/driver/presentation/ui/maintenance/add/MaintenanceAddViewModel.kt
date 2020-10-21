/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.10.2020 19:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.child.Child
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeUi
import kotlinx.coroutines.launch

/**
 *
 */

class MaintenanceAddViewModel(private val repository: IMaintenanceRepository) : BaseViewModel() {
	
	
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
	
	
	
	val lastReplacedChildren: HashMap<SparePart, VehicleSparePart> = hashMapOf()
	
	fun loadLastTimeSparePartReplaced(vin: String, list: List<Child>) {
		viewModelScope.launch(MyDispatchers.io()) {
			
			//clear previous found
			lastReplacedChildren.clear()
			
			list.forEach { child ->
				if (child.sparePart.getSparePartName() != SparePart.OTHER)
					repository.findLastReplaced(
						vin,
						selectedVehicleSystemNode.value!!.toString(),
						child.sparePart.getSparePartName()
					).fold(
						success = { lastReplacedChildren.put(child.sparePart, it) },
						failure = { logWtf(TAG, "$it") }
					)
			}
		}
		
		
	}
	
	fun addMaintenanceHistoryItems(list: List<VehicleSparePart>) {
		viewModelScope.launch(MyDispatchers.io()) {
			repository.addMaintenanceItems(MedriverApp.currentUser, list)
		}
	}
}