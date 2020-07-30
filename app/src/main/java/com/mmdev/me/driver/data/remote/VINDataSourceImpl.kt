package com.mmdev.me.driver.data.remote

import com.mmdev.me.driver.data.api.VINCodeApi

/**
 * This is the documentation block about the class
 */

class VINDataSourceImpl (private val vinCodeApi: VINCodeApi) : IVINDataSource {

	override suspend fun getVehicleByVINCode(VINCode: String) =
		vinCodeApi.getVehicleByVINCodeFromApi(VINCode)


}