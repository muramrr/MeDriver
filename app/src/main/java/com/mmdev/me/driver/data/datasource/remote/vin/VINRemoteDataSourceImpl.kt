/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 17:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.vin

import com.mmdev.me.driver.data.core.base.BaseRemoteDataSource
import com.mmdev.me.driver.data.datasource.remote.api.VINCodeApi

/**
 *
 */

class VINRemoteDataSourceImpl (private val vinCodeApi: VINCodeApi) : BaseRemoteDataSource(),
                                                                     IVINRemoteDataSource {

	override suspend fun getVehicleByVINCode(VINCode: String) =
		safeCallResponse(
			call = { vinCodeApi.getVehicleByVINCodeFromApi(VINCode) },
			errorMessage = "Remote VIN Error"
		)



}