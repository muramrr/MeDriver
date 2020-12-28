/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.data.repository.fetching

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.fetching.FetchingDataSource
import com.mmdev.me.driver.data.sync.download.IDownloader
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
	private val downloader: IDownloader
): BaseRepository(), IFetchingRepository {
	
	//called only on app startup
	override suspend fun getSavedVehicle(vin: String): Vehicle? = vehicleRepository.getSavedVehicle(vin)
	
	
	override fun updateVehicle(
		user: UserDataInfo?, vehicle: Vehicle
	): Flow<SimpleResult<Unit>> = vehicleRepository.addVehicle(user, vehicle)
	
	override suspend fun listenForUpdates(email: String) =
		fetchingDataSource.journalFlow(email).collect {
			logDebug(TAG, "Listening for server journal")
			downloader.downloadNewFromServer(it, email).collect { result ->
				logDebug(TAG, "Downloaded new from server while listening result = $result")
			}
		}
	
}