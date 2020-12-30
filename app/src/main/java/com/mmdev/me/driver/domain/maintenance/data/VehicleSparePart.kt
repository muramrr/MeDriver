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

package com.mmdev.me.driver.domain.maintenance.data

import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import kotlinx.datetime.LocalDateTime

/**
 * Domain data representation
 * All of DTOs and entities (room) converts to this class (repository mappers responsibility)
 *
 * Used in UI
 * @see com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceFragment
 *
 * @param date defines when replacement was made
 * @param dateAdded defines when data is added physically
 *
 * @param systemNode describes affiliation to vehicle system node (eg: Engine, Brakes etc)
 * @param systemNodeComponent describes child component of vehicle system node (eg: Valve, Pads etc)
 */
data class VehicleSparePart(
	var date: LocalDateTime,
	val dateAdded: Long = currentEpochTime(),
	var articulus: String = "",
	var vendor: String = "",
	val systemNode: VehicleSystemNodeType,
	val systemNodeComponent: SparePart,
	var searchCriteria: List<String> = emptyList(),
	var commentary: String = "",
	var moneySpent: Double = 0.0,
	val odometerValueBound: DistanceBound,
	val vehicleVinCode: String
)