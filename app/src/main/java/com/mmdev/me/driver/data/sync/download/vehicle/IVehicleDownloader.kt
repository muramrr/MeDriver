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

package com.mmdev.me.driver.data.sync.download.vehicle

import com.mmdev.me.driver.data.sync.base.IBaseDataDownloader
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Downloader used only to fetch vehicles details data
 */

interface IVehicleDownloader: IBaseDataDownloader {
	
	/**
	 * Download all vehicles from server
	 * @return VIN codes because other downloader
	 * relates on VINs to know which documents to download
	 */
	fun download(email: String): Flow<SimpleResult<List<String>>>
	
}