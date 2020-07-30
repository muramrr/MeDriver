package com.mmdev.me.driver.data.api

import com.mmdev.me.driver.domain.VehicleByVIN
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
	suspend fun getVehicleByVINCodeFromApi(@Path("VINCode") VINCode: String): VehicleByVIN

}