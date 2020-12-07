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