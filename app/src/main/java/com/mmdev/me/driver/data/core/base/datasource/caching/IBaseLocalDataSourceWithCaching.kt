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

import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Every local data source which has permission to write data to server will inherit this
 */

interface IBaseLocalDataSourceWithCaching {
	
	/**
	 * If writing to backend cannot be done at the moment - we will remember need id and write it
	 * to cached operations table
	 * Another time we will try to fetch all table entries to server again
	 */
	suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit>
	
	/**
	 * Same as [cachePendingWriteToBackend] but uses list as a parameter
	 */
	suspend fun cachePendingWriteToBackend(cachedOperations: List<CachedOperation>): SimpleResult<Unit>
	
	/**
	 * Retrieve all cached operations from database
	 */
	suspend fun getCachedOperations(): SimpleResult<List<CachedOperation>>
	
	/**
	 * Delete cached operation
	 * There could be two reasons to delete operation:
	 * 1. Database entity was successfully written to server and we want to delete this from cached
	 * 2. Such entry doesn't exist in database
	 */
	suspend fun deleteCachedOperation(cachedOperation: CachedOperation): SimpleResult<Unit>
	
}