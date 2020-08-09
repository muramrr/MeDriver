/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 17:56
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
import com.mmdev.me.driver.domain.fuel.IFuelRepository

/**
 *
 */

internal class FuelRepositoryImpl (
	private val dataSourceRemote: IFuelRemoteDataSource,
	private val dataSourceLocal: IFuelLocalDataSource,
	private val mappers: FuelDataMappersFacade
) : IFuelRepository {
	
	override suspend fun getFuelInfo(date: String, region: Int) =
		getFuelDataFromLocal(date).fold(
			success = { dto -> ResultState.Success(mappers.mapDbFuelInfoToDomain(dto)) },
			failure = { throwable -> ResultState.Failure(throwable) }
		)
	
	
	private suspend fun getFuelInfoCombined(date: String, region: Int) {
		val a = getFuelDataFromLocal(date)
	}
	
	
	
	//retrieve from remote source
	private suspend fun getFuelDataFromRemote(date: String, region: Int) =
		dataSourceRemote.getFuelInfo(date, region).fold(
			success = {
				dto ->
				//save to db
				for (fuelProviderAndPrices in mappers.mapResponseModelToLocal(dto)) {
					dataSourceLocal.addFuelProvider(fuelProviderAndPrices.fuelProvider).also {
						fuelProviderAndPrices.prices.forEach { fuelPrice ->
							dataSourceLocal.addFuelPrice(fuelPrice)
						}
					}
				}
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

	//retrieve from cache
	private suspend fun getFuelDataFromLocal(date: String) =
		dataSourceLocal.getFuelProvidersAndPrices(date).fold(
			success = {
				dto -> ResultState.Success(dto)
			},
			failure = { throwable -> ResultState.Failure(throwable) }
		)

}