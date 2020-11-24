/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.11.2020 20:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.prices.mappers

import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.dto.FuelPriceAndStationDto
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