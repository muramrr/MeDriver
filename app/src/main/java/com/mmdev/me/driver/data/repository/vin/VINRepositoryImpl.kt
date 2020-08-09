/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 16:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vin

import com.mmdev.me.driver.data.datasource.remote.vin.IVINRemoteDataSource
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.vin.IVINRepository
import com.mmdev.me.driver.domain.vin.VehicleByVIN

/**
 * [IVINRemoteDataSource] implementation
 */

class VINRepositoryImpl (private val dataSourceRemote: IVINRemoteDataSource) : IVINRepository {

	override suspend fun getVehicleByVIN(VINCode: String): SimpleResult<VehicleByVIN> =
		dataSourceRemote.getVehicleByVINCode(VINCode).fold(
			success = { dto -> ResultState.Success(dto.getResult()) },
			failure = { throwable -> ResultState.Failure(throwable) }
		)
}