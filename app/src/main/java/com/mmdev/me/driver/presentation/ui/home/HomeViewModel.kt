/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.11.2020 21:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.domain.home.IHomeRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 *
 */

class HomeViewModel(
	private val repository: IHomeRepository
): BaseViewModel() {
	
	val viewState: MutableLiveData<HomeViewState> = MutableLiveData()
	
	val vehicles: MutableLiveData<List<Vehicle>> = MutableLiveData()
	
	init {
		getMyGarage()
	}
	
	fun getMyGarage() {
		viewModelScope.launch {
			repository.getGarage().fold(
				success = {
					vehicles.postValue(it)
				},
				failure = {
					viewState.postValue(HomeViewState.Error(it.localizedMessage))
				}
			)
		}
	}
	
	
}