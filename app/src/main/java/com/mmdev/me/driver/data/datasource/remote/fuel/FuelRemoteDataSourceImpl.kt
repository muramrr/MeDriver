/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 17:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.fuel

import com.mmdev.me.driver.core.utils.toMap
import com.mmdev.me.driver.data.core.base.BaseRemoteDataSource
import com.mmdev.me.driver.data.datasource.remote.api.FuelApi
import com.mmdev.me.driver.data.datasource.remote.fuel.model.NetworkFuelModelResponse
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * [IFuelRemoteDataSource] implementation
 */

internal class FuelRemoteDataSourceImpl(private val fuelApi: FuelApi) :
		IFuelRemoteDataSource, BaseRemoteDataSource() {
	
	override suspend fun getFuelInfo(date: String): SimpleResult<Map<FuelType, NetworkFuelModelResponse>> =
		safeCallResponse(call = { fetchItems(date, FuelType.values().asIterable()) })
	
	//get all prices for every fuel type
	//response contains list of fuelProviders and their prices for specified fuel type
	private suspend fun fetchItems(
		date:String,
		fuelTypes: Iterable<FuelType>
	): Map<FuelType, NetworkFuelModelResponse> =
		fuelTypes.asFlow()
			.flatMapMerge(concurrency = 3) { fuelType ->
				flow { emit(fuelType to fuelApi.getFuelInfoFromApi(date, fuelType.code)) }
			}.toMap()
}