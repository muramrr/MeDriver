/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.10.2020 14:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle.add

import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.vehicle.add.VehicleAddViewState.Error
import com.mmdev.me.driver.presentation.ui.vehicle.add.VehicleAddViewState.Idle
import com.mmdev.me.driver.presentation.ui.vehicle.add.VehicleAddViewState.Success

/**
 * [ViewState] for [VehicleAddBottomSheet]
 * state [Idle] indicates that current state can't be represented by other states, doing nothing
 * state [Success] indicates that adding operation was successful
 * state [Error] responsible for indicating errors
 */


sealed class VehicleAddViewState: ViewState {
	object Idle: VehicleAddViewState()
	object Loading: VehicleAddViewState()
	object Success: VehicleAddViewState()
	data class Error(val errorMessage: String?): VehicleAddViewState()
	
}