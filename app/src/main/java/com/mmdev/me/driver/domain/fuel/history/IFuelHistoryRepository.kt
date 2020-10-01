/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.09.2020 16:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistory
import com.mmdev.me.driver.domain.user.UserModel
import kotlinx.coroutines.flow.Flow

/**
 * Fuel History repository provides data for [com.mmdev.me.driver.presentation.ui.fuel.history]
 */

interface IFuelHistoryRepository {
	
	suspend fun addFuelHistoryRecord(
		user: UserModel?, history: FuelHistory
	): Flow<SimpleResult<Unit>>
	
	/**
	 * @param size defines how many entries should be loaded to display in UI
	 */
	suspend fun loadFuelHistory(
		vin: String, size: Int?
	): SimpleResult<List<FuelHistory>>
	
	suspend fun removeFuelHistoryRecord(history: FuelHistory): SimpleResult<Unit>
	
}