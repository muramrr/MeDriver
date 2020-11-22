/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:34
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.upload.maintenance

import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * Uploader for maintenance history entries
 */

interface IMaintenanceUploader {
	
	/**
	 * Get all cached operations for VehicleSparePart entries
	 * Concurrently retrieve entity from database and write to server
	 * If entry with such id doesn't exists it will be delete from cached operations
	 */
	suspend fun fetch(email: String): Flow<SimpleResult<Unit>>
	
}