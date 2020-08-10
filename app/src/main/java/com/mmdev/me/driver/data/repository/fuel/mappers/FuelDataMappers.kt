/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 18:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.mappers

import com.mmdev.me.driver.data.core.mappers.mapNullInputList
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.data.datasource.remote.fuel.model.NetworkFuelModelResponse
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.model.FuelSummary

/**
 * contains mappers between layers [data -> domain]
 * mappers between sources [remoteModel -> dbEntity], [dbEntity -> DomainModel]
 *
 * DTO = Data Transfer Object
 * DB = database
 * DM = Domain Model
 */

typealias response = Map<FuelType, NetworkFuelModelResponse>




//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapFuelPriceResponseToEntity(): (response) -> List<FuelPriceEntity> = {
	response -> mapperFuelPriceDto(response)
}

private fun mapperFuelPriceDto(input: response): List<FuelPriceEntity> {
	val listOfFuelPrices = mutableListOf<FuelPriceEntity>()

	for ((fuelType, networkFuelModelResponse) in input) {
		networkFuelModelResponse.result.run {
			//get every fuelProvider with price and generate separate objects price and provider
			networkFuelProviders.forEach { fuelProvider ->
				listOfFuelPrices.add(
					FuelPriceEntity(
						fuelProviderTitle = fuelProvider.brand,
						fuelProviderSlug = fuelProvider.slug,
						price = fuelProvider.price,
						type = fuelType.code,
						updatedDate = this.pricesLastUpdatedDate
					)
				)

			}
		}
	}

	return listOfFuelPrices
}

//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapSummaryResponseToEntity(): (response) -> List<FuelSummaryEntity> = {
	response -> mapperFuelSummaryDto(response)
}

private fun mapperFuelSummaryDto(input: response): List<FuelSummaryEntity> {
	val listOfSummaryEntity = mutableListOf<FuelSummaryEntity>()
	
	for ((fuelType, networkFuelModelResponse) in input) {
		
		listOfSummaryEntity.add(
			FuelSummaryEntity(
				type = fuelType.code,
				minPrice= networkFuelModelResponse.result.fuelSummaryResponse[0].minPrice,
				maxPrice = networkFuelModelResponse.result.fuelSummaryResponse[0].maxPrice,
				avgPrice = networkFuelModelResponse.result.fuelSummaryResponse[0].avgPrice,
				updatedDate = networkFuelModelResponse.result.pricesLastUpdatedDate
			)
		)
	}
	
	return listOfSummaryEntity
}

//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapFuelPriceResponseToDm(): (response, fuelType: FuelType) -> List<FuelPrice> = {
		response, fuelType -> mapperFuelPriceResponseToDm(response, fuelType)
}

private fun mapperFuelPriceResponseToDm(input: response, fuelType: FuelType): List<FuelPrice> {
	if (input.containsKey(fuelType)) {
		input[fuelType]?.result!!.run {
			return mapNullInputList(networkFuelProviders) { fuelProvider ->
				FuelPrice(
					title = fuelProvider.brand,
					slug = fuelProvider.slug,
					price = fuelProvider.price.toString(),
					type = fuelType.code,
					date = this.pricesLastUpdatedDate
				)
			}
		}
	}
	else return emptyList()
}

//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapFuelSummaryEntityToDm(): (List<FuelSummaryEntity>) -> List<FuelSummary> = { dto ->
	mapNullInputList(dto) { entity ->
		FuelSummary(
			type = entity.type,
			minPrice = entity.minPrice,
			maxPrice = entity.maxPrice,
			avgPrice = entity.avgPrice,
			updatedDate = entity.updatedDate
		)
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapFuelPriceEntityToDm(): (List<FuelPriceEntity>) -> List<FuelPrice> = { dto ->
	mapNullInputList(dto) { entity ->
		FuelPrice(
			title = entity.fuelProviderTitle,
			slug = entity.fuelProviderSlug,
			price = entity.price.toString(),
			type = entity.type,
			date = entity.updatedDate
		)
	}
}
