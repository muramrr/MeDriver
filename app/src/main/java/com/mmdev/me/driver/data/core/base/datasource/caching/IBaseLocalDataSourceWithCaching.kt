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