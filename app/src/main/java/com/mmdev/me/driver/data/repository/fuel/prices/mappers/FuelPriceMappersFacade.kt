/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.prices.mappers

import com.mmdev.me.driver.data.core.mappers.mapNullInputList
import com.mmdev.me.driver.data.core.mappers.mapNullInputListToSet
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPricesEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelSummaryEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.model.NetworkFuelModelResponse
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import com.mmdev.me.driver.domain.fuel.prices.model.FuelSummary


/**
 * MappersFacade for multiple mappers used in FuelPricesRepositoryImpl
 * contains mappers between layers [data -> domain]
 * mappers between sources [remoteModel -> dbEntity], [dbEntity -> DomainModel]
 *
 * DTO = Data Transfer Object
 * DB = database
 * DM = Domain Model
 */

private typealias response = Map<FuelType, NetworkFuelModelResponse>

class FuelPriceMappersFacade {
	
	
	//network dto -> db entity
	//////////////////////////////////////////////////////////////////////////////////////////////////
	val mapFuelResponseToDb: (response, String) -> List<FuelStationAndPricesEntity> = {
			response, date -> mapFuelProvidersDto(response, date)
	}
	
	private fun mapFuelProvidersDto(input: response, date: String): List<FuelStationAndPricesEntity> {
		val listOfFuelStationAndPrices = mutableListOf<FuelStationAndPricesEntity>()
		val listOfFuelPrices = mutableListOf<FuelPriceEntity>()
		val setOfFuelStationsDb = mutableSetOf<FuelStationEntity>()
		
		for ((fuelType, networkFuelModelResponse) in input) {
			networkFuelModelResponse.result.run {
				//get every fuelProvider with price and generate separate objects price and provider
				networkFuelStations.forEach { networkFuelStation ->
					//add price to general list
					listOfFuelPrices.add(
						FuelPriceEntity(
							fuelStationId = networkFuelStation.slug,
							price = networkFuelStation.price,
							type = fuelType.code
						)
					)
					//check if set contains FuelStationEntity
					//if not -> add FuelStationEntity to setOf()
					//this is guarantee that it will be stored only ones
					if (setOfFuelStationsDb.find { it.slug == networkFuelStation.slug } == null) {
						setOfFuelStationsDb.add(
							FuelStationEntity(
								brandTitle = networkFuelStation.brand,
								slug = networkFuelStation.slug,
								updatedDate = date
							)
						)
					}
				}
			}
		}
		
		//combine FuelStationEntity and listOf<FuelPriceEntity> into FuelStationAndPrices
		for (fuelStation in setOfFuelStationsDb) {
			listOfFuelStationAndPrices.add(
				FuelStationAndPricesEntity(
					fuelStation = fuelStation,
					prices = listOfFuelPrices.filter { it.fuelStationId == fuelStation.slug })
			)
		}
		
		setOfFuelStationsDb.clear()
		listOfFuelPrices.clear()
		
		return listOfFuelStationAndPrices
	}
	
	//network dto -> db entity
	//////////////////////////////////////////////////////////////////////////////////////////////////
	val mapFuelResponseSummaryToDb: (response) -> List<FuelSummaryEntity> = {
			response -> mapFuelSummaryDto(response)
	}
	
	private fun mapFuelSummaryDto(input: response): List<FuelSummaryEntity> {
		val listOfSummaryEntity = mutableListOf<FuelSummaryEntity>()
		
		for ((fuelType, networkFuelModelResponse) in input) {
			
			listOfSummaryEntity.add(
				FuelSummaryEntity(
					type = fuelType.code,
					minPrice = networkFuelModelResponse.result.fuelSummaryResponse[0].minPrice,
					maxPrice = networkFuelModelResponse.result.fuelSummaryResponse[0].maxPrice,
					avgPrice = networkFuelModelResponse.result.fuelSummaryResponse[0].avgPrice,
					updatedDate = networkFuelModelResponse.result.pricesLastUpdatedDate
				)
			)
		}
		
		return listOfSummaryEntity
	}
	
	
	//network dto -> dm
	//////////////////////////////////////////////////////////////////////////////////////////////////
	val mapFuelResponseToDm: (response) -> List<FuelStationWithPrices> = {
			response -> makeMapperFuelResponseToDm(response)
	}
	
	private fun makeMapperFuelResponseToDm(input: response): List<FuelStationWithPrices> {
		val listOfFuelPrices = mutableListOf<FuelPrice>()
		val setOfFuelStationsDm = mutableSetOf<FuelStationWithPrices>()
		
		for ((fuelType, networkFuelModelResponse) in input) {
			
			networkFuelModelResponse.result.run {
				listOfFuelPrices.clear()
				
				//get every fuelProvider with price and generate separate objects price and provider
				networkFuelStations.forEach { networkFuelStation ->
					
					//check if set contains FuelStation
					//if not -> add FuelStation to setOf()
					//this is guarantee that it will be stored only ones
					if (setOfFuelStationsDm.find {
								it.fuelStation.slug == networkFuelStation.slug
							} == null) {
						setOfFuelStationsDm.add(
							FuelStationWithPrices(
								FuelStation(
									brandTitle = networkFuelStation.brand,
									slug = networkFuelStation.slug,
									updatedDate = pricesLastUpdatedDate
								)
							)
						)
					}
					
					//find FuelStation in setOf and add associated FuelPrice
					setOfFuelStationsDm.forEach { stationWithPrices ->
						if (stationWithPrices.fuelStation.slug == networkFuelStation.slug)
							stationWithPrices.prices.add(
								FuelPrice(type = fuelType, price = networkFuelStation.price)
							)
					}
					
				}
				
			}
		}
		
		
		listOfFuelPrices.clear()
		
		return setOfFuelStationsDm.toList()
	}
	
	
	//db dto -> dm
	//////////////////////////////////////////////////////////////////////////////////////////////////
	val mapDbSummariesToDm: (List<FuelSummaryEntity>) -> List<FuelSummary> = { mapFuelSummaryDm(it) }
	
	private fun mapFuelSummaryDm(input: List<FuelSummaryEntity>): List<FuelSummary> =
		mapNullInputList(input) {
			FuelSummary(
				type = it.type,
				minPrice = it.minPrice,
				maxPrice = it.maxPrice,
				avgPrice = it.avgPrice,
				updatedDate = it.updatedDate
			)
		}
	
	//db dto -> dm
	//////////////////////////////////////////////////////////////////////////////////////////////////
	val mapDbFuelStationToDm: (List<FuelStationAndPricesEntity>) -> List<FuelStationWithPrices> = {
		fuelProviderDto -> mapFuelStationDm(fuelProviderDto) {
			listPricesDto -> mapNullInputListToSet(listPricesDto) {
				priceDto -> mapPriceDm(priceDto)
			}
		}
	}
	
	private fun mapFuelStationDm(
		input: List<FuelStationAndPricesEntity>,
		mapPrices: (List<FuelPriceEntity>?) -> Set<FuelPrice>
	): List<FuelStationWithPrices> =
		mapNullInputList(input) {
			FuelStationWithPrices(
				fuelStation = FuelStation(
					brandTitle = it.fuelStation.brandTitle, slug = it.fuelStation.slug,
					updatedDate = it.fuelStation.updatedDate
				),
				prices = mapPrices(it.prices).toHashSet()
			)
		}
	
	private fun mapPriceDm (input: FuelPriceEntity): FuelPrice =
		FuelPrice(price = input.price, type = input.type)
	
	
}

