/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.08.2020 19:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel


import com.mmdev.me.driver.core.utils.DateConverter
import com.mmdev.me.driver.core.utils.logDebug
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.local.fuel.IFuelLocalDataSource
import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.mappers.FuelDataMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.IFuelRepository
import com.mmdev.me.driver.domain.fuel.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.model.FuelStationWithPrices
import java.util.*

/**
 * [IFuelRepository] implementation
 */

internal class FuelRepositoryImpl (
	private val dataSourceRemote: IFuelRemoteDataSource,
	private val dataSourceLocal: IFuelLocalDataSource,
	private val mappers: FuelDataMappersFacade
) : BaseRepository(), IFuelRepository {
	
	
	companion object {
		//how many entries we can load per request
		private const val historyEntriesLimit = 20
	}
	//start position history entries loading
	private var historyOffset = 0
	
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
		return getFuelDataFromLocal().fold(
			//get from local database
			success = { dm -> ResultState.Success(dm)},
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
				for (fuelStationAndPrices in mappers.mapFuelResponseToDb(dto)) {
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
	private suspend fun getFuelDataFromLocal() =
		dataSourceLocal.getFuelStationsAndPrices().fold(
			success = { dto ->
				if (dto.isNotEmpty()) ResultState.Success(mappers.mapDbFuelStationToDm(dto))
				else ResultState.Failure(Exception("Empty cache"))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	
	override suspend fun getFuelHistory(): SimpleResult<List<FuelHistoryRecord>> =
		dataSourceLocal.getFuelHistory(historyEntriesLimit, historyOffset).fold(
			success = { dto ->
				ResultState.Success(mappers.mapDbHistoryToDm(dto)).also {
					//update start pos
					historyOffset += it.data.size
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	override suspend fun addFuelHistoryEntry(fuelHistoryRecord: FuelHistoryRecord) =
		dataSourceLocal.insertFuelHistoryEntry(mappers.mapDmHistoryToDb(fuelHistoryRecord))
	
	override suspend fun removeFuelHistoryEntry(fuelHistoryRecord: FuelHistoryRecord) =
		dataSourceLocal.deleteFuelHistoryEntry(mappers.mapDmHistoryToDb(fuelHistoryRecord))
}