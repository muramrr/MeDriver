/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.10.2020 18:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel for [FuelHistoryFragment]
 */

class FuelHistoryViewModel(private val repository: IFuelHistoryRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<FuelHistoryViewState> = MutableLiveData()
	
	//called when new entry was added
	val shouldBeUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
	
	//indicates is history empty or not
	val isHistoryEmpty: MutableLiveData<Boolean> = MutableLiveData()
	
	private val loadedHistory = mutableListOf<FuelHistory>()
	
	
	
	/**
	 * [repository] call
	 * @param size defines how many records should be loaded
	 * if [size] is not specified we are probably loading initial data
	 * if [size] is specified then we are probably paginating existing data
	 * If response contains empty list on initial data -> change [isHistoryEmpty] value
	 *
	 * todo: make pagination in both sides. loadNext, loadPrev (this one works only loadNext)
	 */
	fun getHistoryRecords(size: Int? = null) {
		viewModelScope.launch {
			
			viewState.postValue(FuelHistoryViewState.Loading)
			
			repository.loadFuelHistory(MedriverApp.currentVehicleVinCode, size).fold(
				success = { data ->
					if (size == null) {
						//in case below no matter if list is empty or not
						viewState.postValue(FuelHistoryViewState.Init(data = data))
						
						//handle empty state visibility
						isHistoryEmpty.value = data.isEmpty()
					} // if size was specified
					else viewState.postValue(FuelHistoryViewState.Paginate(data = data))
					logDebug(TAG,"history empty? = ${isHistoryEmpty.value}")
					loadedHistory.addAll(data)
				},
				failure = {
					if (loadedHistory.isEmpty()) isHistoryEmpty.value = true
					viewState.postValue(FuelHistoryViewState.Error(it.localizedMessage!!))
				}
			)
			
		}
	}
	
}