/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 15:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.vin

import com.mmdev.me.driver.data.api.VINCodeApi
import com.mmdev.me.driver.data.core.base.BaseDataSourceRemote

/**
 *
 */

class VINDataSourceRemoteImpl (private val vinCodeApi: VINCodeApi) : BaseDataSourceRemote(),
                                                                     IVINDataSourceRemote {

	override suspend fun getVehicleByVINCode(VINCode: String) =
		safeCallResponse(
			call = { vinCodeApi.getVehicleByVINCodeFromApi(VINCode) },
			errorMessage = "Remote VIN Error"
		)



}