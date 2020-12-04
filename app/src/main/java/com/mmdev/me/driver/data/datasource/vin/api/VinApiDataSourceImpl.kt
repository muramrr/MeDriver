/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vin.api

import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.datasource.vin.api.dto.VehicleByVinResponse
import com.mmdev.me.driver.data.datasource.vin.api.retrofit.VinCodeApi
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IVinApiDataSource] implementation
 */

class VinApiDataSourceImpl (private val vinCodeApi: VinCodeApi) :
		BaseDataSource(), IVinApiDataSource {

	override suspend fun getVehicleByVin(vinCode: String): SimpleResult<VehicleByVinResponse> =
		safeCall(TAG) { vinCodeApi.getVehicleByVinCode(vinCode) }
	
}