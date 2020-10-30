/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.10.2020 17:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history.add

import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.fuel.history.add.FuelHistoryAddViewState.Error
import com.mmdev.me.driver.presentation.ui.fuel.history.add.FuelHistoryAddViewState.Success

/**
 * [ViewState] for [FuelHistoryAddDialog]
 * state [Success] indicates that adding operation was successful
 * state [Error] responsible for indicating errors
 */

sealed class FuelHistoryAddViewState: ViewState {
	data class Success(val odometerBound: DistanceBound): FuelHistoryAddViewState()
	data class Error(val errorMessage: String?): FuelHistoryAddViewState()
}