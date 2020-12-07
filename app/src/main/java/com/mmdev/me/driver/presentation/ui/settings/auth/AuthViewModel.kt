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
import com.mmdev.me.driver.domain.user.auth.ISettingsRepository
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
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
		if (!inputEmail.value.isNullOrBlank())
			viewModelScope.launch {
				
				viewState.postValue(AuthViewState.Loading)
				try {
					repository.resetPassword(inputEmail.value!!).collect { result ->
						result.fold(
							success = { viewState.postValue(AuthViewState.Success.ResetPassword) },
							failure = {
								viewState.postValue(
									AuthViewState.Error.ResetPassword(it.localizedMessage)
								)
							}
						)
					}
				}
				catch (e: NullPointerException) {
					viewState.postValue(AuthViewState.Error.ResetPassword(e.message))
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
			viewModelScope.launch {
				
				viewState.postValue(AuthViewState.Loading)
				try {
					repository.signIn(inputEmail.value!!, inputPassword.value!!).collect { result ->
						result.fold(
							success = {
								viewState.postValue(AuthViewState.Success.SignIn)
								clearInput()
							},
							failure = {
								viewState.postValue(AuthViewState.Error.SignIn(it.localizedMessage))
							}
						)
					}
				}
				catch (e: NullPointerException) {
					viewState.postValue(AuthViewState.Error.SignIn(e.message))
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
			viewState.postValue(AuthViewState.Loading)
			//if passwords are same
			try {
				if (inputPasswordAreSameAsConfirm.value!!) {
					
					viewModelScope.launch {
						
						repository.signUp(inputEmail.value!!, inputPassword.value!!).collect { result ->
							result.fold(
								success = {
									viewState.postValue(AuthViewState.Success.SignUp)
									clearInput()
								},
								failure = {
									viewState.postValue(
										AuthViewState.Error.SignUp(it.localizedMessage)
									)
								}
							)
						}
					}
				}
			}
			catch (e: NullPointerException) {
				viewState.postValue(AuthViewState.Error.SignUp(e.message))
			}
		}
		else viewState.postValue(AuthViewState.Error.SignUp("Check input fields"))
	}
	
	/**
	 * Clear [inputEmail], [inputPassword], [inputPasswordConfirm]
	 */
	private fun clearInput() {
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