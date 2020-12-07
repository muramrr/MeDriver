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
