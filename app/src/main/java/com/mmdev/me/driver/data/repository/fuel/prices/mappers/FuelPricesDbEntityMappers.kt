/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.prices.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.domain.fuel.prices.data.FuelSummary

/**
 * In [FuelStationAndPrices], [FuelSummaryEntity] -> Out: [FuelStationWithPrices], [FuelSummary]
 */

object FuelPricesDbEntityMappers {
	
	
	fun listFuelStationAndPricesToDomain(input: List<FuelStationAndPrices>): List<FuelStationWithPrices> =
		mapList(input) {
			FuelStationWithPrices(
				fuelStation = stationEntityToDomain(it.fuelStation),
				prices = listPriceEntitiesToDomain(it.prices).toHashSet()
			)
		}
	
	/** Out: [FuelPrice] */
	private fun priceEntityToDomain(entity: FuelPriceEntity): FuelPrice =
		FuelPrice(price = entity.price, typeCode = entity.typeCode)
	
	private fun listPriceEntitiesToDomain(input: List<FuelPriceEntity>): List<FuelPrice> =
		mapList(input) { priceEntityToDomain(it)}
	
	/** Out: [FuelStation] */
	private fun stationEntityToDomain(entity: FuelStationEntity): FuelStation =
		FuelStation(
			brandTitle = entity.brandTitle,
			slug = entity.slug,
			updatedDate = entity.updatedDate
		)
	
	/** Out: [FuelSummary] */
	fun summaryEntityToDomain(entity: FuelSummaryEntity): FuelSummary =
		FuelSummary(
			typeCode = entity.typeCode,
			minPrice = entity.minPrice,
			maxPrice = entity.maxPrice,
			avgPrice = entity.avgPrice,
			updatedDate = entity.updatedDate
		)
	
}