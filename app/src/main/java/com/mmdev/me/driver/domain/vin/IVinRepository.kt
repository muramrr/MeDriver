package com.mmdev.me.driver.domain.vin

import com.mmdev.me.driver.domain.VehicleByVIN

/**
 * This is the documentation block about the class
 */

interface IVINRepository {

	suspend fun getVehicleByVIN(VINCode: String) : VehicleByVIN

}