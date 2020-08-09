/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 16:34
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel

import com.mmdev.me.driver.domain.core.SimpleResult

/**
 *
 */

interface IFuelRepository {

	suspend fun getFuelInfo(date: String, region: Int = 3) : SimpleResult<List<FuelProvider>>
	
}