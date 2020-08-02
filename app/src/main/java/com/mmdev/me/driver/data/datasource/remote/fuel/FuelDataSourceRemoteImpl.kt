/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 16:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.fuel

import com.mmdev.me.driver.core.utils.toMap
import com.mmdev.me.driver.data.api.FuelApi
import com.mmdev.me.driver.data.core.ResponseState
import com.mmdev.me.driver.data.core.base.BaseDataSourceRemote
import com.mmdev.me.driver.domain.fuel.FuelModelResponse
import com.mmdev.me.driver.domain.fuel.FuelType
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 *
 */

class FuelDataSourceRemoteImpl(private val fuelApi: FuelApi) : IFuelDataSourceRemote, BaseDataSourceRemote() {
	
	override suspend fun getFuelInfo(date: String, fuelType: Int,
	                                 region: Int): ResponseState<Map<FuelType, FuelModelResponse>> =
		safeCallResponse(call = {
			fetchItems(date, FuelType.values().asIterable(), region)
		},
		                 errorMessage = "Remote Fuel Error"
		)
	
	private suspend fun fetchItems(
		date:String,
		itemIds: Iterable<FuelType>,
		region: Int
	): Map<FuelType, FuelModelResponse> =
		itemIds.asFlow()
			.flatMapMerge(concurrency = 3) { itemId ->
				flow { emit(itemId to fuelApi.getFuelInfoFromApi(date, itemId.code, region)) }
			}.toMap()
}