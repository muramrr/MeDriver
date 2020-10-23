/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.10.2020 18:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add

import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewState.Error
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewState.Loading
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewState.Success

/**
 * state [Loading] controls if loading animation should be displayed
 * state [Success] indicates that adding operation was successful
 * state [Error] responsible for indicating errors
 */

sealed class MaintenanceAddViewState: ViewState {
	object Idle: MaintenanceAddViewState()
	object Loading: MaintenanceAddViewState()
	object Success: MaintenanceAddViewState()
	data class Error(val errorMessage: String?): MaintenanceAddViewState()
}