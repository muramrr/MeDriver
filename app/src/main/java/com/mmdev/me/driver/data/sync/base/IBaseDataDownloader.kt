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

package com.mmdev.me.driver.data.sync.base

import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Base behaviour for all separated downloaders, except journal
 *
 */

interface IBaseDataDownloader {
	
	/**
	 * If maintenance history entry was deleted on server from one device, we should delete it
	 * from current device also
	 *
	 * @param id defines documentId which comes from server
	 */
	suspend fun deleteSingle(email: String, id: String): SimpleResult<Unit>
	
	/**
	 * Download only one entry from maintenance history collection from server and import it
	 * directly to local database
	 *
	 * @param id defines documentId which we should download
	 */
	fun downloadSingle(email: String, vin: String, id: String): Flow<SimpleResult<Unit>>
	
	/**
	 * Clear whole fuel history data from local database
	 */
	suspend fun clear(): SimpleResult<Unit>
	
}