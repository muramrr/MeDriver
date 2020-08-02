/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 16:47
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository

import com.mmdev.me.driver.data.core.ResponseState
import com.mmdev.me.driver.data.datasource.remote.fuel.IFuelDataSourceRemote
import com.mmdev.me.driver.domain.core.RepositoryState
import com.mmdev.me.driver.domain.fuel.IFuelRepository

/**
 *
 */

class FuelRepositoryImpl (private val dataSourceRemote: IFuelDataSourceRemote) : IFuelRepository {
	
	override suspend fun getFuelInfo(date: String, region: Int) =
		
		when (val dataSourceResult =
			dataSourceRemote.getFuelInfo(date, region)) {
			
			is ResponseState.Success -> RepositoryState.Success(dataSourceResult.data)
			
			is ResponseState.Error -> RepositoryState.Error(
					dataSourceResult.errorMessage,
					dataSourceResult.throwable?.localizedMessage
			)
		}
	
}