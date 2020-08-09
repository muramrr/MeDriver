/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 15:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.mappers

import com.mmdev.me.driver.data.core.mappers.mapNullInputList
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderAndPrices
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.data.datasource.remote.fuel.model.NetworkFuelModelResponse
import com.mmdev.me.driver.domain.fuel.FuelProvider
import com.mmdev.me.driver.domain.fuel.FuelSummary
import com.mmdev.me.driver.domain.fuel.FuelType

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
fun makeFuelProvidersMapperDto(): (response) -> List<FuelProviderAndPrices> = {
	response -> mapFuelProvidersDto(response)
}

private fun mapFuelProvidersDto(input: response): List<FuelProviderAndPrices> {
	val listOfFuelProviderAndPrices = mutableListOf<FuelProviderAndPrices>()
	val listOfFuelPrices = mutableListOf<FuelPriceEntity>()
	val setOfFuelProviders = mutableSetOf<FuelProviderEntity>()
	
	for ((fuelType, networkFuelModelResponse) in input) {
		networkFuelModelResponse.result.run {
			//get every fuelProvider with price and generate separate objects price and provider
			networkFuelProviders.forEach { fuelProvider ->
				//add price to general list
				listOfFuelPrices.add(
						FuelPriceEntity(
								fuelProvider.slug,
								fuelProvider.price,
								fuelType.code
						)
				)
				//add fuel provider to set, this is guarantee that it will be stored only ones
				//but note: allocated objects not only once
				setOfFuelProviders.add(
						FuelProviderEntity(
								fuelProvider.brand,
								fuelProvider.slug,
								pricesLastUpdatedDate
						)
				)
			}
		}
	}
	
	//assign fuel prices to fuelProvider
	for (fuelProvider in setOfFuelProviders) {
		listOfFuelProviderAndPrices.add(
				FuelProviderAndPrices(
						fuelProvider,
						listOfFuelPrices.filter {
							it.fuelProviderId == fuelProvider.slug
						}
				)
		)
	}
	
	return listOfFuelProviderAndPrices
}

//////////////////////////////////////////////////////////////////////////////////////////////////
fun makeFuelSummaryMapperDto(): (response) -> List<FuelSummaryEntity> = {
	response -> mapFuelSummaryDto(response)
}

private fun mapFuelSummaryDto(input: response): List<FuelSummaryEntity> {
	val listOfSummaryEntity = mutableListOf<FuelSummaryEntity>()
	
	for ((fuelType, networkFuelModelResponse) in input) {
		
		listOfSummaryEntity.add(
				FuelSummaryEntity(
						type = fuelType.code,
						minPrice= networkFuelModelResponse.result.fuelSummaryResponse[0].minPrice,
						maxPrice = networkFuelModelResponse.result.fuelSummaryResponse[0].maxPrice,
						avgPrice = networkFuelModelResponse.result.fuelSummaryResponse[0].avgPrice,
						updatedDate = networkFuelModelResponse.result.pricesLastUpdatedDate)
		)
	}
	
	return listOfSummaryEntity
}

//////////////////////////////////////////////////////////////////////////////////////////////////
fun makeFuelSummaryMapperDm(): (List<FuelSummaryEntity>) -> List<FuelSummary> = { mapFuelSummaryDm(it) }

private fun mapFuelSummaryDm(input: List<FuelSummaryEntity>): List<FuelSummary> =
	mapNullInputList(input) {
		FuelSummary(type = it.type,
		            minPrice = it.minPrice,
		            maxPrice = it.maxPrice,
		            avgPrice = it.avgPrice,
		            updatedDate = it.updatedDate
		)
	}

//////////////////////////////////////////////////////////////////////////////////////////////////
fun makeFuelProvidersMapperDm(): (List<FuelProviderAndPrices>) -> List<FuelProvider> = {
	fuelProviderDto -> mapFuelProvidersDm(fuelProviderDto) {
	listPricesDto ->
		mapNullInputList(listPricesDto) { priceDto ->
			mapPriceDm(priceDto)
		}
	}
}

private fun mapFuelProvidersDm(
	input: List<FuelProviderAndPrices>,
	mapPrices: (List<FuelPriceEntity>?) -> List<FuelProvider.FuelPrice>
): List<FuelProvider> =
	mapNullInputList(input) {
		FuelProvider(
				brandTitle = it.fuelProvider.brandTitle,
				slug = it.fuelProvider.slug,
				prices = mapPrices(it.prices)
		)
	}

private fun mapPriceDm (input: FuelPriceEntity): FuelProvider.FuelPrice =
	FuelProvider.FuelPrice(type = input.type, price = input.price)