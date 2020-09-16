/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.09.2020 03:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import com.mmdev.me.driver.presentation.core.ViewState

/**
 *
 */

internal sealed class AuthViewState: ViewState {
	object Loading : AuthViewState()
	sealed class Error(errorMsg: String?) : AuthViewState() {
		data class ResetPassword(val errorMsg: String?) : Error(errorMsg)
		data class SendVerification(val errorMsg: String?) : Error(errorMsg)
		data class SignIn(val errorMsg: String?) : Error(errorMsg)
		data class SignUp(val errorMsg: String?) : Error(errorMsg)
	}
	
	sealed class Success : AuthViewState() {
		object ResetPassword : Success()
		object SendVerification : Success()
		object SignIn : Success()
		object SignUp : Success()
	}
}