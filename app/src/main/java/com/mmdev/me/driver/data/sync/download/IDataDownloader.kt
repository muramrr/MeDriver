/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.12.2020 19:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download

import com.mmdev.me.driver.data.core.firebase.ServerOperation
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 *
 */

interface IDataDownloader {
	suspend fun importData(email: String): Flow<SimpleResult<Unit>>
	suspend fun fetchNewFromServer(email: String): Flow<SimpleResult<Unit>>
	suspend fun downloadNewFromServer(
		operations: List<ServerOperation>,
		email: String
	): Flow<SimpleResult<Unit>>
}