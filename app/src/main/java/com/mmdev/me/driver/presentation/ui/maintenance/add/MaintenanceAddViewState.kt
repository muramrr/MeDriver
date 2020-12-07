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

package com.mmdev.me.driver.presentation.ui.maintenance.add

import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewState.*

/**
 * [ViewState] for [MaintenanceAddBottomSheet]
 * state [Idle] indicates that nothing is really happening and adding process is in queue
 * state [Success] indicates that adding operation was successful
 * state [Error] responsible for indicating errors
 */

sealed class MaintenanceAddViewState: ViewState {
	object Idle: MaintenanceAddViewState()
	data class Success(val data: VehicleSparePart): MaintenanceAddViewState()
	data class Error(val errorMessage: String?): MaintenanceAddViewState()
}