/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.12.2020 19:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fetching

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.fetching.FetchingDataSource
import com.mmdev.me.driver.data.sync.download.IDataDownloader
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fetching.IFetchingRepository
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/**
 * [IFetchingRepository] implementation
 */

class FetchingRepositoryImpl(
	private val fetchingDataSource: FetchingDataSource,
	private val vehicleRepository: IVehicleRepository,
	private val downloader: IDataDownloader
): BaseRepository(), IFetchingRepository {
	
	//called only on app startup
	override suspend fun getSavedVehicle(vin: String): Vehicle? = vehicleRepository.getSavedVehicle(vin)
	
	
	override suspend fun updateVehicle(
		user: UserDataInfo?, vehicle: Vehicle
	): Flow<SimpleResult<Unit>> = vehicleRepository.addVehicle(user, vehicle)
	
	override suspend fun listenForUpdates(email: String) =
		fetchingDataSource.flow(email).collect {
			downloader.downloadNewFromServer(it, email)
		}
	
	
}