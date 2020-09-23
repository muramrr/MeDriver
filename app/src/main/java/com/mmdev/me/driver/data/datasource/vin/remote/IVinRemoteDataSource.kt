/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 21:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vin.remote

import com.mmdev.me.driver.data.datasource.vin.remote.dto.VehicleByVinResponse
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Datasource to retrieve vehicle info by typed VinCode
 */

interface IVinRemoteDataSource {

	suspend fun getVehicleByVin(vinCode: String) : SimpleResult<VehicleByVinResponse>

}