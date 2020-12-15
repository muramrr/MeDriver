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
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.CACHE_OPERATIONS_TABLE} WHERE tableName = :table")
	suspend fun getPendingOperations(table: String): List<CachedOperation>
	
	@Delete
	suspend fun deleteOperation(cachedOperation: CachedOperation)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.CACHE_OPERATIONS_TABLE} WHERE recordId = :id")
	suspend fun deleteByOperationId(id: String)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOperation(cachedOperation: CachedOperation)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOperation(cachedOperation: List<CachedOperation>)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.CACHE_OPERATIONS_TABLE}")
	suspend fun clearHistory()
	
}