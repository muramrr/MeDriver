/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 17:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vin.remote

import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.vin.remote.api.VinCodeApi
import com.mmdev.me.driver.data.datasource.vin.remote.dto.VehicleByVinResponse
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IVinRemoteDataSource] implementation
 */

class VinRemoteDataSourceImpl (private val vinCodeApi: VinCodeApi) :
		BaseDataSource(), IVinRemoteDataSource {

	override suspend fun getVehicleByVin(vinCode: String): SimpleResult<VehicleByVinResponse> =
		safeCall(TAG) { vinCodeApi.getVehicleByVinCode(vinCode) }
	
}