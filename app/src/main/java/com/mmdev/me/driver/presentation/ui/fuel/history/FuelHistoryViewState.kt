/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.11.2020 19:39
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState.Error
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState.Init
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState.Loading

/**
 * state [Loading] controls if loading animation should be displayed
 * state [Init] indicates that we are loading data first time
 * state [Paginate] indicates that we already loaded some initial data and load more
 * state [Error] responsible for indicating errors
 */

sealed class FuelHistoryViewState: ViewState {
	object Loading: FuelHistoryViewState()
	data class Init(val data: List<FuelHistory>): FuelHistoryViewState()
	data class LoadPrevious(val data: List<FuelHistory>): FuelHistoryViewState()
	data class LoadNext(val data: List<FuelHistory>): FuelHistoryViewState()
	data class Error(val errorMessage: String): FuelHistoryViewState()
}