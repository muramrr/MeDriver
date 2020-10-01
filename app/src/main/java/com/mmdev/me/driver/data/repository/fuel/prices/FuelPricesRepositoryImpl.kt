/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.09.2020 19:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.prices


import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.fuel.prices.local.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.IFuelPricesRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices

/**
 * [IFuelPricesRepository] implementation
 */

class FuelPricesRepositoryImpl (
	private val localDataSource: IFuelPricesLocalDataSource,
	private val remoteDataSource: IFuelPricesRemoteDataSource,
	private val mappers: FuelPriceMappersFacade
) : BaseRepository(), IFuelPricesRepository {
	
	/**
	 * Combine [getFuelDataFromRemote] and [getFuelDataFromLocal]
	 * If cache retrieving is successful -> emit success
	 * else catch failure and make a network request
	 * If network request fails -> emit failure from [getFuelDataFromRemote]
	 */
	override suspend fun getFuelStationsWithPrices(date: String): SimpleResult<List<FuelStationWithPrices>> {
		
		logInfo(TAG, "get prices for $date")
		
		return getFuelDataFromLocal(date).fold(
			//get from local database
			success = { dm -> ResultState.Success(dm) },
			//if failure (throwable or emptyList) -> request from network
			failure = {
				logError(message = it.message ?: "Boundary local cache error")
				getFuelDataFromRemote(date)
			})
	}
	
	//retrieve FuelPrices from remote source
	private suspend fun getFuelDataFromRemote(date: String): SimpleResult<List<FuelStationWithPrices>> =
		remoteDataSource.requestFuelPrices(date).fold(
			success = { dto ->
				//save to db
				with(mappers.listApiDtosToDbEntities(dto, date)){
					localDataSource.addFuelStationsAndPrices(this.first, this.second)
				}
				//after saving -> retrieve from database again
				getFuelDataFromLocal(date)
				
				//ResultState.Success(mappers.mapFuelResponseToDm(dto))
				
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

	//retrieve FuelStationAndPrices from cache (room database)
	private suspend fun getFuelDataFromLocal(date: String): SimpleResult<List<FuelStationWithPrices>> =
		localDataSource.getFuelStationsAndPrices(date).fold(
			success = { dto ->
				if (dto.isNotEmpty()) ResultState.Success(mappers.dbFuelStationAndPricesToDomains(dto))
				else ResultState.Failure(Exception("Empty cache"))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
}