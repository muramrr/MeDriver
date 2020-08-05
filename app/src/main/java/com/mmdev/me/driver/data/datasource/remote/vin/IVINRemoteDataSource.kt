/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 17:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.vin

import com.mmdev.me.driver.data.core.ResponseState
import com.mmdev.me.driver.domain.vin.VinCodeResponse

/**
 * This is the documentation block about the class
 */

interface IVINRemoteDataSource {

	suspend fun getVehicleByVINCode(VINCode: String) : ResponseState<VinCodeResponse>

}