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

package com.mmdev.me.driver.presentation.ui.settings.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.domain.user.ISettingsRepository
import com.mmdev.me.driver.domain.user.SignInStatus
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.utils.extensions.combineWith
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *
 */

class AuthViewModel(private val repository: ISettingsRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<AuthViewState> = MutableLiveData()
	
	val inputEmail: MutableLiveData<String?> = MutableLiveData()
	val inputPassword: MutableLiveData<String?> = MutableLiveData()
	val inputPasswordConfirm: MutableLiveData<String?> = MutableLiveData()
	
	val inputPasswordAreSameAsConfirm: LiveData<Boolean?> =
		inputPassword.combineWith(inputPasswordConfirm) { inputPass, inputPassConf ->
			if (!inputPass.isNullOrBlank() && !inputPassConf.isNullOrBlank()) inputPass == inputPassConf
			else null
		}
	
	
	/**
	 * Try to Reset password
	 * else catch null input error
	 */
	fun resetPassword() {
		if (!inputEmail.value.isNullOrBlank()) {
			
			viewState.postValue(AuthViewState.Loading)
			
			viewModelScope.launch {
				repository.resetPassword(inputEmail.value!!).collect { result ->
					result.fold(
						success = { viewState.postValue(AuthViewState.ResetPassword.Success) },
						failure = { viewState.postValue(AuthViewState.ResetPassword.Error(it.localizedMessage)) }
					)
				}
			}
		}
	}
	
	/**
	 * Try to Sign In
	 * else catch null input error
	 */
	fun signIn() {
		//check both input fields are not null or blank
		if (!inputEmail.value.isNullOrBlank() && !inputPassword.value.isNullOrBlank()) {
			
			viewState.postValue(AuthViewState.Loading)
			
			viewModelScope.launch {
				repository.signIn(inputEmail.value!!, inputPassword.value!!).collect { result ->
					result.fold(
						success = { status ->
							when(status) {
								SignInStatus.Loading -> {} // do nothing we already have loading state
								SignInStatus.Fetching -> viewState.value = AuthViewState.SignIn.Processing
								SignInStatus.Deleting -> MainActivity.currentVehicle = null
								SignInStatus.Downloading -> viewState.value = AuthViewState.SignIn.Downloading
								SignInStatus.Finished -> viewState.value = AuthViewState.SignIn.Success
								else -> { logDebug(TAG, "sign in status = $status")}
							}
						},
						failure = { viewState.postValue(AuthViewState.SignIn.Error(it.localizedMessage)) }
					)
				}
			}
		}
	}
	
	/**
	 * Try to Sign Up
	 * else catch null input error
	 */
	fun signUp() {
		//check email input and password equality are not null or blank
		if (!inputEmail.value.isNullOrBlank() && inputPasswordAreSameAsConfirm.value != null) {
			//if passwords are same
			if (inputPasswordAreSameAsConfirm.value!!) {
				
				viewState.postValue(AuthViewState.Loading)
				
				viewModelScope.launch {
					
					repository.signUp(inputEmail.value!!, inputPassword.value!!).collect { result ->
						result.fold(
							success = { viewState.postValue(AuthViewState.SignUp.Success) },
							failure = { viewState.postValue(AuthViewState.SignUp.Error(it.localizedMessage)) }
						)
					}
				}
			}
		}
		else viewState.postValue(AuthViewState.SignUp.Error("Check input fields"))
	}
	
	/**
	 * Clear only [inputPassword], [inputPasswordConfirm]
	 */
	fun clearPasswordsInput() {
		inputPassword.postValue(null)
		inputPasswordConfirm.postValue(null)
	}
}