/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState.*

/**
 * state [Loading] controls if loading animation should be displayed
 * state [Init] indicates that we are loading data first time
 * state [Paginate] indicates that we already loaded some initial data and load more
 * state [Error] responsible for indicating errors
 */

sealed class FuelHistoryViewState: ViewState {
	object Loading : FuelHistoryViewState()
	data class Init(val data: List<FuelHistoryRecord>) : FuelHistoryViewState()
	data class InsertNewOne(val data: List<FuelHistoryRecord>) : FuelHistoryViewState()
	data class Paginate(val data: List<FuelHistoryRecord>) : FuelHistoryViewState()
	data class Error(val errorMessage: String) : FuelHistoryViewState()
}