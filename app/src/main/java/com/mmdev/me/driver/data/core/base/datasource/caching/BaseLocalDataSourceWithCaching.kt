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
	
	override suspend fun deleteCachedOperationById(id: String): SimpleResult<Unit> =
		safeCall(TAG) { cache.deleteByOperationId(id) }.also {
			logInfo(TAG, "Deleting operation, id = $id")
		}
}