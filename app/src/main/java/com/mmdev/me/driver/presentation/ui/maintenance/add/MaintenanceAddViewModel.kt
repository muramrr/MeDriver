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

package com.mmdev.me.driver.presentation.ui.maintenance.add

import androidx.lifecycle.MutableLiveData
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.child.Child
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeUi

/**
 *
 */

class MaintenanceAddViewModel : BaseViewModel() {
	
	
	val parentShouldBeUpdated = MutableLiveData(false)
	
	val selectedParentNode = MutableLiveData<ParentNodeUi>()
	val selectedVehicleSystemNode = MutableLiveData<VehicleSystemNodeType>()
	
	val multiSelectState = MutableLiveData(false)
	val selectedChildren = MutableLiveData<List<Child>?>()
	
//	val isSameDateForAll: MutableLiveData<Boolean> = MutableLiveData(false)
//	val isSameOdometerForAll: MutableLiveData<Boolean> = MutableLiveData(false)
	
	fun selectParentNode(parent: VehicleSystemNodeType, parentUi: ParentNodeUi) {
		selectedVehicleSystemNode.postValue(parent)
		selectedParentNode.postValue(parentUi)
	}
}