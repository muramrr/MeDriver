/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.11.2020 15:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance

import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.Error
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.Init
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.LoadNext
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceHistoryViewState.Loading


/**
 * state [Loading] controls if loading animation should be displayed
 * state [Init] indicates that we are loading data first time
 * state [LoadNext] indicates that we already loaded some initial data and load more
 * state [Error] responsible for indicating errors
 */

sealed class MaintenanceHistoryViewState: ViewState {
	object Loading: MaintenanceHistoryViewState()
	data class Init(val data: List<VehicleSparePart>): MaintenanceHistoryViewState()
	data class LoadPrevious(val data: List<VehicleSparePart>): MaintenanceHistoryViewState()
	data class LoadNext(val data: List<VehicleSparePart>): MaintenanceHistoryViewState()
	data class Filter(val data: List<VehicleSparePart>): MaintenanceHistoryViewState()
	data class Error(val errorMessage: String?): MaintenanceHistoryViewState()
}