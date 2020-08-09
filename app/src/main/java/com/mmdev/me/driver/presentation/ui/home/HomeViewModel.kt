/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 18:06
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.domain.vin.IVINRepository
import com.mmdev.me.driver.domain.vin.VehicleByVIN
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 *
 */

class HomeViewModel(private val repository: IVINRepository): BaseViewModel() {



	val vehicleByVIN: MutableLiveData<VINViewState> = MutableLiveData()

	sealed class VINViewState: ViewState {
		object Loading : VINViewState()
		data class Success(val data: VehicleByVIN) : VINViewState()
		data class Error(val errorMessage: String) : VINViewState()
	}

	fun getVehicleByVIN(VINCode: String) {

		viewModelScope.launch {

			vehicleByVIN.postValue(VINViewState.Loading)
			withTimeout(30000) {
				
				repository.getVehicleByVIN(VINCode).fold(
					success = { vehicleByVIN.postValue(VINViewState.Success(data = it)) },
					failure = { vehicleByVIN.postValue(VINViewState.Error(it.localizedMessage!!)) }
				)
			}
		}
		
	}

}