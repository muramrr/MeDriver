package com.mmdev.me.driver.domain


data class VehicleByVIN(
	val make: String,
	val model: String
)

data class VinCodeResponse (val results: List<VehicleByVIN>)
