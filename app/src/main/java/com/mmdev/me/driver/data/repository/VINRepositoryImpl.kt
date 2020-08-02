/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 15:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository

import com.mmdev.me.driver.data.core.ResponseState
import com.mmdev.me.driver.data.datasource.remote.vin.IVINDataSourceRemote
import com.mmdev.me.driver.domain.core.RepositoryState
import com.mmdev.me.driver.domain.vin.IVINRepository
import com.mmdev.me.driver.domain.vin.VehicleByVIN

/**
 *
 */

class VINRepositoryImpl (private val vinDataSourceRemote: IVINDataSourceRemote) : IVINRepository {

	override suspend fun getVehicleByVIN(VINCode: String): RepositoryState<VehicleByVIN> =

		when (val dataSourceResult =
				vinDataSourceRemote.getVehicleByVINCode(VINCode)) {

			is ResponseState.Success -> RepositoryState.Success(dataSourceResult.data.getResult())

			is ResponseState.Error -> RepositoryState.Error(
					dataSourceResult.errorMessage,
					dataSourceResult.throwable?.localizedMessage
			)
		}

}