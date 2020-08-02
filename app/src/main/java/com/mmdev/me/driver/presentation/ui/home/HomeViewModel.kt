/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 16:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.domain.core.RepositoryState
import com.mmdev.me.driver.domain.vin.IVINRepository
import com.mmdev.me.driver.domain.vin.VehicleByVIN
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch

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

			when (val result = repository.getVehicleByVIN(VINCode)) {

				is RepositoryState.Success ->
					vehicleByVIN.postValue(VINViewState.Success(data = result.data))

				is RepositoryState.Error ->
					vehicleByVIN.postValue(VINViewState.Error(result.errorMessage))

			}
		}
		
	}

}