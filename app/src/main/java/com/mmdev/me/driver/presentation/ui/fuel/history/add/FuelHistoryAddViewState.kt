/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.11.2020 17:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history.add

import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.fuel.history.add.FuelHistoryAddViewState.*

/**
 * [ViewState] for [FuelHistoryAddDialog]
 * state [Success] indicates that adding operation was successful
 * state [Error] responsible for indicating errors
 */

sealed class FuelHistoryAddViewState: ViewState {
	data class Success(val fuelHistory: FuelHistory): FuelHistoryAddViewState()
	data class Error(val errorMessage: String?): FuelHistoryAddViewState()
}