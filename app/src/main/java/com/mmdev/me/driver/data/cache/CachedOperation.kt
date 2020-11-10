/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 17:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.cache

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase

/**
 * Describes database entity to store cached operation info
 */

@Entity(tableName = MeDriverRoomDatabase.CACHE_OPERATIONS_TABLE)
data class CachedOperation(
	val tableName: String,
	@PrimaryKey
	val recordId: String,
	val reasonAddingToCache: String
)