/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.08.2020 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleByVIN(
	@SerialName("Make")
	val vehicleBrand: String = "",
	@SerialName("Model")
	val vehicleModel: String = "",
	@SerialName("ModelYear")
	val vehicleProducedYear: String = "",
	@SerialName("PlantCountry")
	val vehicleProducedPlant: String = "",
	@SerialName("DisplacementL")
	val vehicleEngineCapacity: String = ""
)

@Serializable
data class VinCodeResponse (
	@SerialName("Results")
	val results: List<VehicleByVIN> = emptyList()
) {
	fun getResult(): VehicleByVIN = if (results.isNotEmpty()) results[0] else VehicleByVIN()
}
