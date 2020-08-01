/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.08.20 15:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain

import com.google.gson.annotations.SerializedName


data class VehicleByVIN(
	@SerializedName("Make")
	val vehicleBrand: String = "",
	@SerializedName("Model")
	val vehicleModel: String = "",
	@SerializedName("ModelYear")
	val vehicleProducedYear: String = "",
	@SerializedName("PlantCountry")
	val vehicleProducedPlant: String = "",
	@SerializedName("DisplacementL")
	val vehicleEngineCapacity: String = ""
)

data class VinCodeResponse (
	@SerializedName("Results")
	val results: List<VehicleByVIN> = emptyList()
) {
	fun getResult(): VehicleByVIN = if (results.isNotEmpty()) results[0] else VehicleByVIN()
}
