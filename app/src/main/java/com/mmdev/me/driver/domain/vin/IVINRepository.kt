/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 14:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vin

import com.mmdev.me.driver.domain.core.RepositoryState

/**
 *
 */

interface IVINRepository {
	
	suspend fun getVehicleByVIN(VINCode: String) : RepositoryState<VehicleByVIN>
	
}