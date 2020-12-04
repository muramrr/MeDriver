/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download.vehicle

import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 *
 */

interface IVehicleDownloader {
	suspend fun download(email: String): Flow<SimpleResult<List<String>>>
	suspend fun downloadSingle(email: String, vin: String): Flow<SimpleResult<Unit>>
	suspend fun clear(): SimpleResult<Unit>
}