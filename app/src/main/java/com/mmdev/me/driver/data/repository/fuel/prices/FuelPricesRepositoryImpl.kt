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

package com.mmdev.me.driver.data.repository.fuel.prices


import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.fuel.prices.api.IFuelPricesApiDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.domain.fuel.prices.data.Region

/**
 * [IFuelPricesRepository] implementation
 */

class FuelPricesRepositoryImpl (
	private val localDataSource: IFuelPricesLocalDataSource,
	private val apiDataSource: IFuelPricesApiDataSource,
	private val mappers: FuelPriceMappersFacade
) : BaseRepository(), IFuelPricesRepository {
	
	/**
	 * If cache retrieving is successful -> return success
	 * else catch failure and make a network request
	 * If network request fails -> return failure from [getFuelDataFromRemote]
	 */
	override suspend fun getFuelStationsWithPrices(
		date: String, region: Region
	): SimpleResult<List<FuelStationWithPrices>> {
		
		logInfo(TAG, "get prices for $date")
		
		return getFuelDataFromLocal(date, region).fold(
			//get from local database
			success = { dm -> ResultState.Success(dm) },
			//if failure (throwable or emptyList) -> request from network
			failure = {
				logError(TAG, it.message ?: "Boundarycache error")
				getFuelDataFromRemote(date, region)
			})
	}
	
	//retrieve FuelPrices from remote source
	private suspend fun getFuelDataFromRemote(
		date: String, region: Region
	): SimpleResult<List<FuelStationWithPrices>> =
		apiDataSource.requestFuelPrices(date, region).fold(
			success = { dto ->
				//save to db
				with(mappers.listApiDtosToDbEntities(dto, date, region)){
					localDataSource.addFuelStationsAndPrices(this.first, this.second)
				}
				//after saving -> retrieve from database again
				getFuelDataFromLocal(date, region)
				
				//ResultState.Success(mappers.mapFuelResponseToDm(dto))
				
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

	//retrieve FuelStationAndPrices from cache (room database)
	private suspend fun getFuelDataFromLocal(
		date: String, region: Region
	): SimpleResult<List<FuelStationWithPrices>> =
		localDataSource.getFuelStationsAndPrices(date, region.id).fold(
			success = { dto ->
				if (dto.isNotEmpty()) ResultState.Success(mappers.listEntityToDomains(dto))
				else ResultState.Failure(Exception("Empty cache"))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
}