/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 17:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.model.FuelPrice

/**
 * Fuel repository which provides requested data to UI
 */

interface IFuelRepository {

	suspend fun getFuelPrices(fuelType: FuelType, date: String) : SimpleResult<List<FuelPrice>>
	
}