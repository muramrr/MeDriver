package com.mmdev.me.driver.domain.fuel.model

import com.mmdev.me.driver.domain.fuel.FuelType

/**
 * Domain model
 */

data class FuelSummary(
	val type: FuelType,
	val minPrice: String,
	val maxPrice: String,
	val avgPrice: String,
	val updatedDate: String
){
	constructor(
		type: Int,
		minPrice: String,
		maxPrice: String,
		avgPrice: String,
		updatedDate: String
	) : this(
		type = FuelType.values().find { it.code == type } ?: FuelType.A95,
		minPrice = minPrice,
		maxPrice = maxPrice,
		avgPrice = avgPrice,
		updatedDate = updatedDate
	)
}