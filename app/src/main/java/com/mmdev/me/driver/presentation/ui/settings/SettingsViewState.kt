/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.10.2020 15:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import com.mmdev.me.driver.presentation.core.ViewState

/**
 * Settings send verification email view state
 */

sealed class SettingsViewState: ViewState {
	
	sealed class Error(errorMsg: String?): SettingsViewState() {
		data class SendVerification(val errorMsg: String?): Error(errorMsg)
	}
	
	sealed class Success: SettingsViewState() {
		object SendVerification: Success()
	}
	
}