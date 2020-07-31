/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 17:32
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
import kotlinx.coroutines.launch

/**
 *
 */

class HomeViewModel(private val repository: IVINRepository): BaseViewModel() {



	val vehicleByVIN: MutableLiveData<VehicleByVIN> = MutableLiveData()

	fun getVehicleByVIN(VINCode: String) {
		viewModelScope.launch {
			val data = repository.getVehicleByVIN(VINCode)
			vehicleByVIN.postValue(data)
		}



	}

}