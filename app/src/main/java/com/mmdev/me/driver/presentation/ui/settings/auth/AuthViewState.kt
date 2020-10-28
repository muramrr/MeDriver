/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.10.2020 15:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings.auth

import com.mmdev.me.driver.presentation.core.ViewState

/**
 *
 */

sealed class AuthViewState: ViewState {
	object Loading: AuthViewState()
	
	sealed class Error(errorMsg: String?): AuthViewState() {
		data class ResetPassword(val errorMsg: String?): Error(errorMsg)
		data class SignIn(val errorMsg: String?): Error(errorMsg)
		data class SignUp(val errorMsg: String?): Error(errorMsg)
	}
	
	sealed class Success: AuthViewState() {
		object ResetPassword: Success()
		object SignIn: Success()
		object SignUp: Success()
	}
}