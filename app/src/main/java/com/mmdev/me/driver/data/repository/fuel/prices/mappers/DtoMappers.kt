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

import com.mmdev.me.driver.data.datasource.fuel.prices.api.dto.FuelPriceAndStationDto
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.data.Region

/**
 * In [response] -> Out: [FuelStationAndPrices], [FuelStationWithPrices], [FuelSummaryEntity], [FuelSummary]
 */

object DtoMappers {
	
	/** Out: [FuelStationAndPrices] */
	fun listApiDtoToDbEntity(
		input: response, date: String, region: Region
	): Pair<List<FuelStationEntity>, List<FuelPriceEntity>> {
		
		val fuelPriceEntities = mutableListOf<FuelPriceEntity>()
		val fuelStationEntities = hashSetOf<FuelStationEntity>()
		
		for ((fuelType, networkFuelModelResponse) in input) {
			with(networkFuelModelResponse.result) {
				//get every fuelStation with price and generate separate objects price and station
				fuelPriceAndStationDtos.forEach { fuelPriceAndStationDto ->
					val split = splitFuelPriceAndStationDto(
						date, fuelType, fuelPriceAndStationDto, region
					)
					//add price to general list
					fuelPriceEntities.add(split.first)
					
					//add FuelStationEntity to setOf()
					//this is guarantee that it will be stored only ones
					fuelStationEntities.add(split.second)
				}
			}
		}
		
		return Pair(fuelStationEntities.toList(), fuelPriceEntities)
	}
	
	private fun splitFuelPriceAndStationDto(
		date: String, fuelType: FuelType, input: FuelPriceAndStationDto, region: Region
	): Pair<FuelPriceEntity, FuelStationEntity> = Pair(
		FuelPriceEntity(
			fuelStationId = input.slug + "_${region.id}",
			price = input.price,
			typeCode = fuelType.code
		),
		FuelStationEntity(
			brandTitle = input.brand,
			slug = input.slug,
			updatedDate = date,
			regionId = region.id
		)
	)
	
	
	fun apiDtoFuelSummaryToDbEntity(input: response): List<FuelSummaryEntity> = input.map {
		FuelSummaryEntity(
			typeCode = it.key.code, minPrice = it.value.result.fuelSummaryDto[0].minPrice,
			maxPrice = it.value.result.fuelSummaryDto[0].maxPrice,
			avgPrice = it.value.result.fuelSummaryDto[0].avgPrice,
			updatedDate = it.value.result.pricesLastUpdatedDate
		)
	}
	
}