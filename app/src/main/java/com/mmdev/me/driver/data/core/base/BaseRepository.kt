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

package com.mmdev.me.driver.data.core.base

import com.mmdev.me.driver.data.cache.CachingReasons
import com.mmdev.me.driver.data.cache.CachingReasons.*
import com.mmdev.me.driver.domain.user.UserDataInfo

/**
 * Base class for all repositories classes
 * Idk why I need it, but just in case
 */

abstract class BaseRepository {
	
	protected val TAG = "mylogs_${javaClass.simpleName}"
	
	/**
	 * Very funny code block, but very useful
	 * Check all criteria before sending data to backend on each step define reason why writing to
	 * backend cannot be done and write aborted operation to cache
	 * All pending operations will be written later as soon as satisfies conditions
	 */
	protected inline fun addToBackend(
		user: UserDataInfo?,
		isInternetAvailable: Boolean,
		cacheOperation: (CachingReasons) -> Unit,
		serverOperation: () -> Unit
	) = if (user != null) {
		if (user.isSubscriptionValid()) {
			
			if (isInternetAvailable) {
				serverOperation.invoke()
			}
			else {
				cacheOperation.invoke(NO_INTERNET)
			}
			
		}
		else {
			cacheOperation.invoke(NO_SUBSCRIPTION)
		}
	}
	else {
		cacheOperation.invoke(NO_USER)
	}
	
}