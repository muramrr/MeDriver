package com.mmdev.me.driver.data.repository

import com.mmdev.me.driver.data.remote.IVINDataSource
import com.mmdev.me.driver.domain.vin.IVINRepository

/**
 * This is the documentation block about the class
 */

class VINRepositoryImpl (private val vinDataSource: IVINDataSource) : IVINRepository {

	override suspend fun getVehicleByVIN(VINCode: String) =
		vinDataSource.getVehicleByVINCode(VINCode)

}