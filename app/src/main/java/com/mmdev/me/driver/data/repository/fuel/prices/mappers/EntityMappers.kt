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
 * In: [FuelStationAndPrices], [FuelSummaryEntity] -> Out: [FuelStationWithPrices], [FuelSummary]
 */

object EntityMappers {
	
	
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