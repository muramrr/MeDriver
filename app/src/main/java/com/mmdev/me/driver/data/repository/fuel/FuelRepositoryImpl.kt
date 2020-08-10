/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 18:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel


import com.mmdev.me.driver.data.datasource.local.fuel.IFuelLocalDataSource
import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.mappers.FuelDataMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.IFuelRepository

/**
 * [IFuelRepository] implementation
 */

internal class FuelRepositoryImpl (
	private val dataSourceRemote: IFuelRemoteDataSource,
	private val dataSourceLocal: IFuelLocalDataSource,
	private val mappers: FuelDataMappersFacade
) : IFuelRepository {
	
	override suspend fun getFuelPrices(fuelType: FuelType, date: String) =
		getFuelPriceBoundary(fuelType, date)
	
	
	private suspend fun getFuelPriceBoundary(fuelType: FuelType, date: String) =
		getFuelDataFromLocal(fuelType, date).fold(
			//get from local database
			success = { dm -> ResultState.Success(dm)},
			//if failure (throwable or emptyList) -> request from network
			failure = { getFuelDataFromRemote(fuelType, date) }
		)
	
	//retrieve from remote source
	private suspend fun getFuelDataFromRemote(fuelType: FuelType, date: String) =
		dataSourceRemote.getFuelInfo(date).fold(
			success = {
				dto ->
				//save to db
				for (fuelPrice in mappers.mapResponsePriceToDb(dto))
					dataSourceLocal.addFuelPrice(fuelPrice)
				ResultState.Success(mappers.mapResponsePriceToDm(dto, fuelType))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

	//retrieve from cache (room database)
	private suspend fun getFuelDataFromLocal(fuelType: FuelType, date: String) =
		dataSourceLocal.getFuelPrices(fuelType, date).fold(
			success = { dto ->
				if (dto.isNotEmpty()) ResultState.Success(mappers.mapDbFuelPriceToDm(dto))
				else ResultState.Failure(Exception("Empty cache"))
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

}