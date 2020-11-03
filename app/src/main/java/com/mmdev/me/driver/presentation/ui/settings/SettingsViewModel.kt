/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.11.2020 19:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.DARK_MODE
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.domain.user.auth.ISettingsRepository
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel attached to SettingsFragment
 * Responsible for sign in/out/up interaction
 */

class SettingsViewModel(private val repository: ISettingsRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<SettingsViewState> = MutableLiveData()
	
	fun setThemeMode(isChecked: Boolean) {
		if (isChecked) MedriverApp.changeThemeMode(DARK_MODE)
		else MedriverApp.changeThemeMode(LIGHT_MODE)
	}
	
	fun setMetricSystem(metricSystem: MetricSystem) = MedriverApp.changeMetricSystem(metricSystem)
	
	
	fun sendEmailVerification(email: String) {
		if (email.isNotBlank()) {
			viewModelScope.launch {
				
				repository.sendEmailVerification(email).collect { result ->
					result.fold(
						success = {
							viewState.postValue(SettingsViewState.Success.SendVerification)
						},
						failure = {
							viewState.postValue(
								SettingsViewState.Error.SendVerification(it.localizedMessage)
							)
						}
					)
				}
				
			}
		}
		else viewState.postValue(SettingsViewState.Error.SendVerification("Email is null"))
	}
	
	fun signOut() = repository.signOut()
	
}