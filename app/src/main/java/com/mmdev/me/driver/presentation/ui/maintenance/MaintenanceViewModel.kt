/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 18:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 *
 */

class MaintenanceViewModel (private val repository: IMaintenanceRepository) : BaseViewModel() {






/* bottom sheet maintenance add */
	
	val selectedVehicleSystemNode: MutableLiveData<VehicleSystemNodeType?> = MutableLiveData()
	val selectedChildComponent: MutableLiveData<SparePart?> = MutableLiveData()
	
	fun selectParentNode(parent: VehicleSystemNodeType) {
		selectedVehicleSystemNode.postValue(parent)
	}
	
	
	fun addMaintenanceHistoryItems(list: List<VehicleSparePart>) {
		viewModelScope.launch(MyDispatchers.io()) {
			repository.addMaintenanceItems(MedriverApp.currentUser, list)
		}
	}
	
	fun loadMaintenanceHistory() {
	
	}
	
	fun clearDialogData() {
		selectedVehicleSystemNode.value = null
		selectedChildComponent.value = null
	}
}