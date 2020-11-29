/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.11.2020 20:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.user.UserDataInfo
import kotlinx.coroutines.flow.Flow

/**
 * Fuel History repository provides data for [com.mmdev.me.driver.presentation.ui.fuel.history]
 */

interface IFuelHistoryRepository {
	
	suspend fun addFuelHistoryRecord(user: UserDataInfo?, history: FuelHistory): Flow<SimpleResult<Unit>>
	
	suspend fun importFuelHistory(user: UserDataInfo?, history: List<FuelHistory>): Flow<SimpleResult<Unit>>
	
	suspend fun getInitFuelHistory(vin: String): SimpleResult<List<FuelHistory>>
	suspend fun getMoreFuelHistory(vin: String): SimpleResult<List<FuelHistory>>
	suspend fun getPreviousFuelHistory(vin: String): SimpleResult<List<FuelHistory>>
	
	suspend fun loadFirstFuelHistoryEntry(vin: String): SimpleResult<FuelHistory?>
	
	
	suspend fun removeFuelHistoryRecord(history: FuelHistory): SimpleResult<Unit>
	
}