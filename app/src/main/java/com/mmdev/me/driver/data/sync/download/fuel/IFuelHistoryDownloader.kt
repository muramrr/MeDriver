/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 00:34
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download.fuel

import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 *
 */

interface IFuelHistoryDownloader {
	suspend fun download(email: String, vin: String): Flow<SimpleResult<Unit>>
	suspend fun downloadSingle(email: String, vin: String, id: String): Flow<SimpleResult<Unit>>
	suspend fun clear(): SimpleResult<Unit>
}