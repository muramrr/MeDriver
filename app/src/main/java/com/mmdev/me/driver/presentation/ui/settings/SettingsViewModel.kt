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

package com.mmdev.me.driver.presentation.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.*
import com.mmdev.me.driver.domain.user.ISettingsRepository
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
		if (isChecked) MedriverApp.themeMode = DARK_MODE
		else MedriverApp.themeMode = LIGHT_MODE
	}
	
	fun setMetricSystem(metricSystem: MetricSystem) {
		MedriverApp.metricSystem = metricSystem
	}
	
	
	fun sendEmailVerification(email: String) {
		if (email.isNotBlank()) {
			viewModelScope.launch {
				
				repository.sendEmailVerification(email).collect { result ->
					result.fold(
						success = {
							viewState.postValue(SettingsViewState.SendVerification.Success)
						},
						failure = {
							viewState.postValue(
								SettingsViewState.SendVerification.Error(it.localizedMessage)
							)
						}
					)
				}
				
			}
		}
		else viewState.postValue(SettingsViewState.SendVerification.Error("Email is null"))
	}
	
	fun signOut() = repository.signOut()
	
}