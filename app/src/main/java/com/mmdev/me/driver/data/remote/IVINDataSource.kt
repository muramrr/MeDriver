package com.mmdev.me.driver.data.remote

import com.mmdev.me.driver.domain.VehicleByVIN

/**
 * This is the documentation block about the class
 */

interface IVINDataSource {

	suspend fun getVehicleByVINCode(VINCode: String) : VehicleByVIN

}