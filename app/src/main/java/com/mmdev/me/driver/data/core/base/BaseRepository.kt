/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.12.2020 18:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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