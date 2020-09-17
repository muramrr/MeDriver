/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.09.2020 02:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.DARK_MODE
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.user.auth.IAuthRepository
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.utils.combineWith
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel attached to SettingsFragment
 * Responsible for sign in/out/up interaction
 */

internal class SettingsViewModel (private val repository: IAuthRepository): BaseViewModel() {
	
	val authViewState: MutableLiveData<AuthViewState> = MutableLiveData()
	
	
	val inputEmail: MutableLiveData<String?> = MutableLiveData()
	val inputPassword: MutableLiveData<String?> = MutableLiveData()
	val inputPasswordConfirm: MutableLiveData<String?> = MutableLiveData()
	
	val inputPasswordAreSameAsConfirm: LiveData<Boolean?> =
		inputPassword.combineWith(inputPasswordConfirm) { inputPass, inputPassConf ->
			if (!inputPass.isNullOrBlank() && !inputPassConf.isNullOrBlank()) inputPass == inputPassConf
			else null
		}
	
	fun setThemeMode(isChecked: Boolean) {
		if (isChecked) MedriverApp.toggleThemeMode(DARK_MODE)
		else MedriverApp.toggleThemeMode(LIGHT_MODE)
	}
	
	fun setMetricSystem(metricSystem: MetricSystem) = MedriverApp.toggleMetricSystem(metricSystem)
	
	/**
	 * Try to Reset password
	 * else catch null input error
	 */
	fun resetPassword() {
		if (!inputEmail.value.isNullOrBlank())
			viewModelScope.launch {
				
				authViewState.postValue(AuthViewState.Loading)
				try {
					repository.resetPassword(inputEmail.value!!).collect {
						when (it) {
							is ResultState.Success -> authViewState.postValue(
								AuthViewState.Success.ResetPassword
							)
							is ResultState.Failure -> authViewState.postValue(
								AuthViewState.Error.ResetPassword(it.error.message)
							)
						}
					}
				}
				catch (e: NullPointerException) {
					authViewState.postValue(AuthViewState.Error.ResetPassword(e.message))
				}
			
			}
	}
	
	fun sendEmailVerification(email: String) {
		if (email.isNotBlank()) {
			viewModelScope.launch {
				
				repository.sendEmailVerification(email).collect { result ->
					result.fold(
						success = { authViewState.postValue(AuthViewState.Success.SendVerification) },
						failure = { throwable ->
							authViewState.postValue(
								AuthViewState.Error.SendVerification(throwable.message)
							)
						}
					)
				}
				
			}
		}
		else authViewState.postValue(AuthViewState.Error.SendVerification("Email is null"))
	}
	
	/**
	 * Try to Sign In
	 * else catch null input error
	 */
	fun signIn() {
		//check both input fields are not null or blank
		if (!inputEmail.value.isNullOrBlank() && !inputPassword.value.isNullOrBlank()) {
			viewModelScope.launch {
				
				authViewState.postValue(AuthViewState.Loading)
				try {
					repository.signIn(inputEmail.value!!, inputPassword.value!!).collect { result ->
						result.fold(
							success = { authViewState.postValue(AuthViewState.Success.SignIn) },
							failure = { throwable ->
								authViewState.postValue(
									AuthViewState.Error.SignIn(throwable.message)
								)
							}
						)
					}
				}
				catch (e: NullPointerException) {
					authViewState.postValue(AuthViewState.Error.SignIn(e.message))
				}
				
			}
		}
	}
	
	fun signOut() = repository.signOut()
	
	/**
	 * Try to Sign Up
	 * else catch null input error
	 */
	fun signUp() {
		//check email input and password equality are not null or blank
		if (!inputEmail.value.isNullOrBlank() && inputPasswordAreSameAsConfirm.value != null) {
			authViewState.postValue(AuthViewState.Loading)
			//if passwords are same
			try {
				if (inputPasswordAreSameAsConfirm.value!!) {
					
					viewModelScope.launch {
						
						repository.signUp(inputEmail.value!!, inputPassword.value!!).collect { result ->
							result.fold(
								success = { authViewState.postValue(AuthViewState.Success.SignUp) },
								failure = {  throwable ->
									authViewState.postValue(
										AuthViewState.Error.SignUp(throwable.message)
									)
								}
							)
						}
					}
				}
			}
			catch (e: NullPointerException) {
				authViewState.postValue(AuthViewState.Error.SignUp(e.message))
			}
		} else authViewState.postValue(AuthViewState.Error.SignUp("Check input fields"))
	}
	
	/**
	 * Clear [inputEmail], [inputPassword], [inputPasswordConfirm]
	 */
	fun clearInput() {
		inputEmail.postValue(null)
		clearPasswordsInput()
	}
	
	/**
	 * Clear only [inputPassword], [inputPasswordConfirm]
	 */
	fun clearPasswordsInput() {
		inputPassword.postValue(null)
		inputPasswordConfirm.postValue(null)
	}
}