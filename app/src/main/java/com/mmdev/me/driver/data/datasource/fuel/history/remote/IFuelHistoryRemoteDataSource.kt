/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 03:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.remote

import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDTO
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Fuel History Datasource which works with Firestore. Used to read/write data from/to backend
 * @param [email: String] identifies user in users collection
 * @param [vin: String] identifies car in user cars collection
 */

interface IFuelHistoryRemoteDataSource {
	
	fun getFuelHistory(email: String, vin: String): Flow<SimpleResult<List<FuelHistoryDTO>>>
	
	fun updateFuelHistoryField(
		email: String,
		vin: String,
		historyId: String,
		field: String,
		value: Any
	): Flow<SimpleResult<Void>>
	
	fun writeFuelHistoryDTO(
		email: String,
		vin: String,
		dto: FuelHistoryDTO
	): Flow<SimpleResult<Unit>>
}