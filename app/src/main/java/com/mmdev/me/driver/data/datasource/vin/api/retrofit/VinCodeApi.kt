/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vin.api.retrofit

import com.mmdev.me.driver.data.datasource.vin.api.dto.VehicleByVinResponse
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Retrieve vehicle by vinCode retrofit api call
 */

interface VinCodeApi {

	private companion object {
		private const val VIN_CODE_URL = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinvalues/"
	}

	@GET("$VIN_CODE_URL{vinCode}?format=json")
	suspend fun getVehicleByVinCode(@Path("vinCode") vinCode: String): VehicleByVinResponse

}