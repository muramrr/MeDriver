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

package com.mmdev.me.driver.data.sync.download

import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperation
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Boundary class for downloading vehicle, maintenance and fuel history data from server
 * Base actions related to downloading data from server
 */

interface IDataDownloader {
	
	/**
	 * Completely delete all data stored in local Room database
	 */
	suspend fun deleteAll()
	
	/**
	 * Import data directly into room, without 'journaling' local operations etc.
	 */
	fun importData(email: String): Flow<SimpleResult<Unit>>
	
	/**
	 * Get all operations that are not synced from server journal and execute [downloadNewFromServer]
	 * Used in [com.mmdev.me.driver.core.sync.download.DownloadWorker]
	 */
	fun fetchNewFromServer(email: String): Flow<SimpleResult<Unit>>
	
	/**
	 * Transform given journal data -> download needed data from server -> import to device
	 *
	 * @param operations contains all server journal docs, which we need to download
	 * @param email current user email
	 */
	fun downloadNewFromServer(
		operations: List<ServerOperation>,
		email: String
	): Flow<SimpleResult<Unit>>
}