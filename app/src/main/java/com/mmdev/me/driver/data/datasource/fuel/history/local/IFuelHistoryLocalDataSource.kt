/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.local

import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Fuel History DataSource which works with local database
 */

interface IFuelHistoryLocalDataSource {
	
	suspend fun getFuelHistory(limit: Int, offset: Int): SimpleResult<List<FuelHistoryEntity>>
	suspend fun insertFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit>
	suspend fun deleteFuelHistoryEntry(fuelHistoryEntity: FuelHistoryEntity): SimpleResult<Unit>
	
}