/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.10.2020 18:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.maintenance.data

import com.mmdev.me.driver.core.utils.currentEpochTime
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