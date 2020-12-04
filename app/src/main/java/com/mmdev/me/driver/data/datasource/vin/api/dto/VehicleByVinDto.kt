/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vin.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleByVin(
	@SerialName("Make")
	val vehicleBrand: String,
	@SerialName("Model")
	val vehicleModel: String,
	@SerialName("ModelYear")
	val vehicleProducedYear: String,
	@SerialName("DisplacementL")
	val vehicleEngineCapacity: String,
	@SerialName("VIN")
	val vehicleVinCode: String
)

@Serializable
data class VehicleByVinResponse (
	@SerialName("Results")
	val results: List<VehicleByVin>
)
