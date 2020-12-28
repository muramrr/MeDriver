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
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.domain.user.ISettingsRepository
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.utils.extensions.combineWith
import kotlinx.coroutines.delay
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
	
	
	fun signIn(isConfirmed: Boolean = false) {
		//check both input fields are not null or blank
		if (!inputEmail.value.isNullOrBlank() && !inputPassword.value.isNullOrBlank()) {
			
			viewState.postValue(AuthViewState.Loading)
			
			viewModelScope.launch {
				if (MedriverApp.savedUserEmail.isNotBlank() && MedriverApp.savedUserEmail != inputEmail.value!! && !isConfirmed)
					viewState.postValue(AuthViewState.SignIn.NeedConfirmation)
				else repository.signIn(inputEmail.value!!, inputPassword.value!!).collect { result ->
					result.fold(
						success = {
							delay(500)
							viewState.postValue(AuthViewState.SignIn.Success)
						},
						failure = { viewState.postValue(AuthViewState.SignIn.Error(it.localizedMessage)) }
					)
				}
			}
		}
	}
	
	
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