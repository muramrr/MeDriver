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
import com.mmdev.me.driver.domain.fuel.prices.data.Region


/**
 * Mapping between FuelPrices and FuelStation DTOs/entities/domain data classes
 */

class FuelPriceMappersFacade {
	
	
	// in: dto, out: * summary entity, prices and stations entity
	fun listApiDtosToDbEntities(
		input: response,
		date: String,
		region: Region
	): Pair<List<FuelStationEntity>, List<FuelPriceEntity>> =
		DtoMappers.listApiDtoToDbEntity(input, date, region)
	
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
	
	fun listSummaryEntitiesToDomains(input: List<FuelSummaryEntity>): List<FuelSummary> =
		mapList(input) { EntityMappers.summaryEntityToDomain(it) }
	
	
	
	// in: FuelStationAndPrices entity, out * domain
	fun listEntityToDomains(input: List<FuelStationAndPrices>): List<FuelStationWithPrices> =
		EntityMappers.listFuelStationAndPricesToDomain(input)
	
	
}

