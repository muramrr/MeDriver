/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 16:17
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
	
	override suspend fun getFuelInfo(
		date: String,
		fuelType: Int,
		region: Int
	): SimpleResult<Map<FuelType, NetworkFuelModelResponse>> =
		safeCallResponse(call = { fetchItems(date, FuelType.values().asIterable(), region) })
	
	private suspend fun fetchItems(
		date:String,
		itemIds: Iterable<FuelType>,
		region: Int
	): Map<FuelType, NetworkFuelModelResponse> =
		itemIds.asFlow()
			.flatMapMerge(concurrency = 3) { itemId ->
				flow { emit(itemId to fuelApi.getFuelInfoFromApi(date, itemId.code, region)) }
			}.toMap()
}