/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 18:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.remote

import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * This datasource responsible to read/write data from/to backend (backup purposes)
 *
 * @param email identifies user in users collection
 * @param vin identifies car in user cars collection
 */

interface IFuelHistoryRemoteDataSource {
	
	fun addFuelHistory(
		email: String,
		vin: String,
		dto: FuelHistoryDto
	): Flow<SimpleResult<Unit>>
	
	fun getFuelHistory(email: String, vin: String): Flow<SimpleResult<List<FuelHistoryDto>>>
	
	fun updateFuelHistoryField(
		email: String,
		vin: String,
		documentId: String,
		field: String,
		value: Any
	): Flow<SimpleResult<Void>>
	
}