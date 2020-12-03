/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 19:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.remote.dto

import com.google.firebase.firestore.PropertyName
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Used to separate data class dependence between data and domain layers
 * Used only to represent data stored on backend
 *
 * @see com.mmdev.me.driver.data.datasource.maintenance.remote.MaintenanceRemoteDataSourceImpl
 */

data class VehicleSparePartDto (
	@PropertyName("commentary") val commentary: String = "",
	@PropertyName("date") val date: String = "",
	@PropertyName("dateAdded") val dateAdded: Long = 0,
	@PropertyName("articulus") val articulus: String = "",
	@PropertyName("vendor") val vendor: String = "",
	@PropertyName("systemNode") val systemNode: String = "",
	@PropertyName("systemNodeComponent") val systemNodeComponent: String = "",
	@PropertyName("searchCriteria") val searchCriteria: List<String> = emptyList(),
	@PropertyName("moneySpent") val moneySpent: Double = 0.0,
	@PropertyName("odometerValue") val odometerValue: DistanceBound = DistanceBound(),
	@PropertyName("vehicleVinCode") val vehicleVinCode: String = ""
)