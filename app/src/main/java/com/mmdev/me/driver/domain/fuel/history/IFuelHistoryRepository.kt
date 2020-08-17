/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord

/**
 * Fuel History repository provides data for [com.mmdev.me.driver.presentation.ui.fuel.history]
 */

interface IFuelHistoryRepository {
	
	suspend fun loadFuelHistory() : SimpleResult<List<FuelHistoryRecord>>
	
	/**
	 * @param entries defines how many entries should be loaded to display in UI
	 */
	suspend fun loadMoreFuelHistory(entries: Int) : SimpleResult<List<FuelHistoryRecord>>
	
	suspend fun addFuelHistoryEntry(fuelHistoryRecord: FuelHistoryRecord)
	
	suspend fun removeFuelHistoryEntry(fuelHistoryRecord: FuelHistoryRecord)
	
}