/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.08.2020 18:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.prices


import com.mmdev.me.driver.core.utils.DateConverter
import com.mmdev.me.driver.core.utils.logDebug
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.local.fuel.prices.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelPricesRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import java.util.*

/**
 * [IFuelPricesRepository] implementation
 */

internal class FuelPricesRepositoryImpl (
	private val dataSourceLocal: IFuelPricesLocalDataSource,
	private val dataSourceRemote: IFuelPricesRemoteDataSource,
	private val mappers: FuelPriceMappersFacade
) : BaseRepository(), IFuelPricesRepository {
	
	
	//network api requests strings
	private lateinit var currentTime: Date
	private var requestDate = ""
	
	
	
	/**
	 * Combine [getFuelDataFromRemote] and [getFuelDataFromLocal]
	 * If cache retrieving is successful -> emit success
	 * else catch failure and make a network request
	 * If network request fails -> emit failure from [getFuelDataFromRemote]
	 */
	override suspend fun getFuelProvidersWithPrices(): SimpleResult<List<FuelStationWithPrices>> {
		currentTime = Calendar.getInstance().time
		requestDate = DateConverter.toFuelPriceRequestString(currentTime).also {
			logDebug(TAG, "date = $it")
		}
		return getFuelDataFromLocal(requestDate).fold(
			//get from local database
			success = { dm -> ResultState.Success(dm) },
			//if failure (throwable or emptyList) -> request from network
			failure = {
				logDebug(message = it.localizedMessage!!)
				getFuelDataFromRemote(requestDate)
			})
	}
	
	//retrieve FuelPrices from remote source
	private suspend fun getFuelDataFromRemote(date: String) =
		dataSourceRemote.getFuelInfo(date).fold(
			success = { dto ->
				//save to db
				for (fuelStationAndPrices in mappers.mapFuelResponseToDb(dto, date)) {
					dataSourceLocal.addFuelStation(fuelStationAndPrices.fuelStation).also {
						fuelStationAndPrices.prices.forEach { fuelPrice ->
							dataSourceLocal.addFuelPrice(fuelPrice)
						}
					}
				}
				ResultState.Success(mappers.mapFuelResponseToDm(dto))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

	//retrieve FuelStationAndPrices from cache (room database)
	private suspend fun getFuelDataFromLocal(date: String) =
		dataSourceLocal.getFuelStationsAndPrices(date).fold(
			success = { dto ->
				if (dto.isNotEmpty()) ResultState.Success(mappers.mapDbFuelStationToDm(dto))
				else ResultState.Failure(Exception("Empty cache"))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
}