/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.07.20 21:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.domain.VehicleByVIN
import com.mmdev.me.driver.domain.vin.IVINRepository
import com.mmdev.me.driver.presentation.ui.common.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 */

class HomeViewModel constructor(private val repository: IVINRepository
	//@Assisted private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

	val vehicleByVIN : MutableLiveData<VehicleByVIN> = MutableLiveData()

	fun getVehicleByVIN(VINCode: String) = viewModelScope.launch {
		withContext(Dispatchers.IO) {
			repository.getVehicleByVIN(VINCode)
		}
	}

}