/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.10.2020 16:24
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
 * MappersFacade for multiple mappers used in FuelPricesRepositoryImpl
 * contains mappers between layers [data -> domain]
 * mappers between sources [remoteModel -> dbEntity], [dbEntity -> DomainModel]
 *
 * DTO = Data Transfer Object
 * DB = database
 * DM = Domain Model
 */

class FuelPriceMappersFacade {
	
	
	// in: dto, out: * summary entity, prices and stations entity
	fun listApiDtosToDbEntities(input: response, date: String):
			Pair<List<FuelStationEntity>, List<FuelPriceEntity>> =
		DtoMappers.listApiDtoToDbEntity(input, date)
	
	fun listApiDtosSummaryToDb(input: response): List<FuelSummaryEntity> =
		DtoMappers.apiDtoFuelSummaryToDbEntity(input)
	
	fun makeMapperFuelResponseToDm(input: response): List<FuelStationWithPrices> {
		val listOfFuelPrices = mutableListOf<FuelPrice>()
		val setOfFuelStationsDm = mutableSetOf<FuelStationWithPrices>()
		
		for ((fuelType, networkFuelModelResponse) in input) {
			
			networkFuelModelResponse.result.run {
				listOfFuelPrices.clear()
				
				//get every fuelStation with prices and generate separate objects price and station
				fuelPriceAndStationDtos.forEach { networkFuelStation ->
					
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
	
	
	// in: summary entity, out: * domain
//	fun summaryEntityToDomain(entity: FuelSummaryEntity): FuelSummary =
//		FuelPricesDbEntityMappers.summaryEntityToDomain(entity)
	
	fun dbSummaryEntitiesToDomains(input: List<FuelSummaryEntity>): List<FuelSummary> =
		mapList(input) { EntityMappers.summaryEntityToDomain(it) }
	
	
	
	// in: FuelStationAndPrices entity, out * domain
	fun dbFuelStationAndPricesToDomains(input: List<FuelStationAndPrices>): List<FuelStationWithPrices> =
		EntityMappers.listFuelStationAndPricesToDomain(input)
	
	
}

