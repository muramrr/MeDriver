/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 17:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.api

import com.mmdev.me.driver.domain.vin.VinCodeResponse
import retrofit2.http.GET
import retrofit2.http.Path



/**
 * Retrofit vehicle by vincode retrieve api call
 */

interface VINCodeApi {

	private companion object {
		private const val VINCODE_URL = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinvalues/"
	}

	@GET("$VINCODE_URL{VINCode}?format=json")
	suspend fun getVehicleByVINCodeFromApi(@Path("VINCode") VINCode: String): VinCodeResponse

}