/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 21:28
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.remote.dto

import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Used to separate data class dependence between data and domain layers
 * Used only to represent data stored on backend
 *
 * @see com.mmdev.me.driver.data.datasource.maintenance.remote.MaintenanceRemoteDataSourceImpl
 */

data class VehicleSparePartDto (
	val date: String = "",
	val dateAdded: Long = 0,
	val articulus: String = "",
	val vendor: String = "",
	val systemNode: String = "",
	val systemNodeComponent: String = "",
	val commentary: String = "",
	val moneySpent: Double = 0.0,
	val odometerValueBound: DistanceBound = DistanceBound(),
	val vehicleVinCode: String = ""
)