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

package com.mmdev.me.driver.presentation.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.domain.fetching.IFetchingRepository
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.user.auth.AuthStatus
import com.mmdev.me.driver.domain.user.auth.IAuthFlowProvider
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * [SharedViewModel] used in every fragment in app. Parent is MainActivity
 * Main responsibilities: Handle LOADING dialog appearance
 * Handle user [AuthStatus]
 * [currentVehicle] mutable value shared across all application
 */

class SharedViewModel(
	private val authProvider: IAuthFlowProvider,
	private val fetcher: IFetchingRepository
) : BaseViewModel() {
	
	companion object {
		var uploadWorkerExecuted = false
	}
	
	val userDataInfo = MutableLiveData<UserDataInfo?>(null)
	
	val currentVehicle = MutableLiveData<Vehicle?>()
	init {
		getSavedVehicle(MedriverApp.currentVehicleVinCode)
		viewModelScope.launch {
			authProvider.getAuthUserFlow().collect {
				userDataInfo.value = it
				if (it != null && it.isPro()) fetcher.listenForUpdates(it.email)
			}
		}
	}
	
	fun getSavedVehicle(vin: String) {
		viewModelScope.launch {
			currentVehicle.postValue(fetcher.getSavedVehicle(vin))
		}
	}
	
	/**
	 * Used in different parts of application
	 * For example: when user adds new fuel history entry or some maintenance changes
	 * this function is triggered to update actual info.
	 */
	fun updateVehicle(user: UserDataInfo?, vehicle: Vehicle) {
		logInfo(TAG, "Updating vehicle..")
		viewModelScope.launch {
			fetcher.updateVehicle(user, vehicle).collect { result ->
				result.fold(
					success = { currentVehicle.postValue(vehicle) },
					failure = { logError(TAG, "$it")}
				)
			}
		}
	}
	
	fun updateUser(user: UserDataInfo) {
		if (user != MainActivity.currentUser!!) {
			
			logWarn(TAG, "Updating user..")
			
			viewModelScope.launch {
				
				authProvider.updateSyncStatus(user).collect { result ->
					result.fold(
						success = { userDataInfo.value = user },
						failure = { logError(TAG, "$it") }
					)
				}
				
			}
		}
	}
	
}