/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState.*

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
	data class Delete(val position: Int): FuelHistoryViewState()
	data class Error(val errorMessage: String?): FuelHistoryViewState()
}