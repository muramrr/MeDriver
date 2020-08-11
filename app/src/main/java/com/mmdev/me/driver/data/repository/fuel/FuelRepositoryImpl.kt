/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 21:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel


import com.mmdev.me.driver.core.utils.logDebug
import com.mmdev.me.driver.data.datasource.local.fuel.IFuelLocalDataSource
import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.mappers.FuelDataMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.IFuelRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * [IFuelRepository] implementation
 */

internal class FuelRepositoryImpl (
	private val dataSourceRemote: IFuelRemoteDataSource,
	private val dataSourceLocal: IFuelLocalDataSource,
	private val mappers: FuelDataMappersFacade
) : IFuelRepository {
	
	private val currentTime = Calendar.getInstance().time
	private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
	private val formattedDate = formatter.format(currentTime)
	
	override suspend fun getFuelProvidersWithPrices()=
		getFuelPriceBoundary()
	
	
	private suspend fun getFuelPriceBoundary() =
		getFuelDataFromLocal(formattedDate).fold(
			//get from local database
			success = { dm -> ResultState.Success(dm)},
			//if failure (throwable or emptyList) -> request from network
			failure = {
				logDebug(message = it.localizedMessage!!)
				getFuelDataFromRemote(formattedDate)
			})
	
	//retrieve from remote source
	private suspend fun getFuelDataFromRemote(date: String) =
		dataSourceRemote.getFuelInfo(date).fold(
			success = { dto ->
				//save to db
				for (fuelProviderAndPrices in mappers.mapFuelResponseToDb(dto)) {
					dataSourceLocal.addFuelProvider(fuelProviderAndPrices.fuelProvider).also {
						fuelProviderAndPrices.prices.forEach { fuelPrice ->
							dataSourceLocal.addFuelPrice(fuelPrice)
						}
					}
				}
				ResultState.Success(mappers.mapFuelResponseToDm(dto))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

	//retrieve from cache (room database)
	private suspend fun getFuelDataFromLocal(date: String) =
		dataSourceLocal.getFuelProvidersAndPrices(date).fold(
			success = { dto ->
				if (dto.isNotEmpty()) ResultState.Success(mappers.mapDbFuelProviderToDm(dto))
				else ResultState.Failure(Exception("Empty cache"))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

}