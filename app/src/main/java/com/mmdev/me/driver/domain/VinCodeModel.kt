/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 17:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain

import com.google.gson.annotations.SerializedName


data class VehicleByVIN(
	@SerializedName("Make")
	val vehicleBrand: String,
	@SerializedName("Model")
	val vehicleModel: String
)

data class VinCodeResponse (
	@SerializedName("Results")
	val results: List<VehicleByVIN>
) {
	fun getResult(): VehicleByVIN = results[0]
}
