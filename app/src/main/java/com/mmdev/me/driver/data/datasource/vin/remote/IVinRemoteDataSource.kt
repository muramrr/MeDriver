/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vin.remote

import com.mmdev.me.driver.data.datasource.vin.remote.dto.VehicleByVinResponse
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Wrapper for retrofit api call
 */

interface IVinRemoteDataSource {
	
	/**
	 * Retrieve vehicle info by typed VinCode
	 */
	suspend fun getVehicleByVin(vinCode: String) : SimpleResult<VehicleByVinResponse>

}