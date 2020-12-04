/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download.journal

import com.mmdev.me.driver.data.core.firebase.ServerOperation
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 *
 */

interface IJournalDownloader {
	
	fun getOperations(
		email: String,
		lastKnownOperationId: Long
	): Flow<SimpleResult<List<ServerOperation>>>
	
}