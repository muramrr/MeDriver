/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.11.2020 21:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.home

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.home.IHomeLocalDataSource
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.home.IHomeRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle

/**
 * [IHomeRepository]
 */

class HomeRepositoryImpl(
	private val localDataSource: IHomeLocalDataSource,
	private val mappers: VehicleMappersFacade
): BaseRepository(), IHomeRepository {
	
	override suspend fun getGarage(): SimpleResult<List<Vehicle>> = localDataSource.getMyGarage().fold(
		success = {
			ResultState.success(mappers.listEntitiesToDomain(it))
		},
		failure = {
			ResultState.failure(it)
		}
	)
	
}