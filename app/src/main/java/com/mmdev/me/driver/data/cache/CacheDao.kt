/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 18:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase

/**
 * Dao interface responsible to retrieve cached operations to write them to backend
 */

@Dao
interface CacheDao {
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.CACHE_OPERATIONS_TABLE}")
	suspend fun getPendingOperations(): List<CachedOperation>
	
	@Delete
	suspend fun deleteOperation(cachedOperation: CachedOperation)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOperation(cachedOperation: CachedOperation)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOperation(cachedOperation: List<CachedOperation>)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.CACHE_OPERATIONS_TABLE}")
	suspend fun clearHistory()
	
}