/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.base.datasource.caching

import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Parent behaviour for all DataSources which interacts with database (local)
 */

abstract class BaseLocalDataSourceWithCaching(
	private val cache: CacheDao
): BaseDataSource(), IBaseLocalDataSourceWithCaching {
	
	protected abstract val table: String
	
	override suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit> =
		safeCall(TAG) { cache.insertOperation(cachedOperation) }.also {
			logWarn(TAG, "Some of the conditions are not allowing to write to backend, caching operation:$cachedOperation")
		}
	
	override suspend fun cachePendingWriteToBackend(cachedOperations: List<CachedOperation>): SimpleResult<Unit> =
		safeCall(TAG) { cache.insertOperation(cachedOperations) }.also {
			logWarn(TAG, "Some of the conditions are not allowing to write to backend, caching operations:$cachedOperations")
		}
	
	override suspend fun getCachedOperations(): SimpleResult<List<CachedOperation>> = safeCall(TAG) {
		cache.getPendingOperations(table)
	}
	
	override suspend fun deleteCachedOperation(cachedOperation: CachedOperation): SimpleResult<Unit> =
		safeCall(TAG) { cache.deleteOperation(cachedOperation) }.also {
			logInfo(TAG, "Deleting operation: $cachedOperation")
		}
	
}