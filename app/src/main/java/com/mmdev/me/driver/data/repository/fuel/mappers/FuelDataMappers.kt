/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 21:08
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
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.model.FuelProvider
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

//network dto -> db entity
//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapFuelResponseToEntity(): (response) -> List<FuelProviderAndPrices> = {
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
						fuelProviderId = fuelProvider.slug,
						price = fuelProvider.price,
						type = fuelType.code
					)
				)
				//add fuel provider to set, this is guarantee that it will be stored only ones
				//but note: allocated objects not only once
				setOfFuelProviders.add(
					FuelProviderEntity(
						brandTitle = fuelProvider.brand,
						slug = fuelProvider.slug,
						updatedDate = pricesLastUpdatedDate
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

//network dto -> db entity
//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapFuelResponseSummaryToEntity(): (response) -> List<FuelSummaryEntity> = {
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


//network dto -> dm
//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapFuelResponseToDm(): (response) -> List<FuelProvider> = {
		response -> makeMapperFuelResponseToDm(response)
}

private fun makeMapperFuelResponseToDm(input: response): List<FuelProvider> {
	val listOfFuelPrices = mutableListOf<FuelProvider.FuelPrice>()
	val setOfFuelProviders = mutableSetOf<FuelProvider>()
	
	for ((fuelType, networkFuelModelResponse) in input) {
		networkFuelModelResponse.result.run {
			listOfFuelPrices.clear()
			
			//get every fuelProvider with price and generate separate objects price and provider
			networkFuelProviders.forEach { fuelProvider ->
				
				//add price to general list
				listOfFuelPrices.add(
					FuelProvider.FuelPrice(
						type = fuelType,
						price = fuelProvider.price
					)
				)
				
				//add fuel provider to set, this is guarantee that it will be stored only ones
				//but note: allocated objects not only once
				setOfFuelProviders.add(
					FuelProvider(
						brandTitle = fuelProvider.brand,
						slug = fuelProvider.slug,
						prices = listOfFuelPrices,
						updatedDate = pricesLastUpdatedDate
					)
				)
			}
		}
	}
	
	return setOfFuelProviders.toList()
}


//db dto -> dm
//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapDbFuelSummaryToDm(): (List<FuelSummaryEntity>) -> List<FuelSummary> = { mapFuelSummaryDm(it) }

private fun mapFuelSummaryDm(input: List<FuelSummaryEntity>): List<FuelSummary> =
	mapNullInputList(input) {
		FuelSummary(type = it.type,
		            minPrice = it.minPrice,
		            maxPrice = it.maxPrice,
		            avgPrice = it.avgPrice,
		            updatedDate = it.updatedDate
		)
	}

//db dto -> dm
//////////////////////////////////////////////////////////////////////////////////////////////////
fun mapDbFuelProvidersToDm(): (List<FuelProviderAndPrices>) -> List<FuelProvider> = {
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
			prices = mapPrices(it.prices),
			updatedDate = it.fuelProvider.updatedDate
		)
	}

private fun mapPriceDm (input: FuelPriceEntity): FuelProvider.FuelPrice =
	FuelProvider.FuelPrice(type = input.type, price = input.price)