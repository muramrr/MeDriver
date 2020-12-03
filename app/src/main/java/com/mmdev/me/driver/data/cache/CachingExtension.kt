/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 18:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.cache

import com.mmdev.me.driver.data.cache.CachingReason.*
import com.mmdev.me.driver.domain.user.UserDataInfo

/**
 * Very funny code block, but very useful
 * Check all criteria before sending data to backend on each step define reason why writing to
 * backend cannot be done and write aborted operation to cache
 * All pending operations will be written later as soon as satisfies conditions
 */

inline fun addToBackend(
	user: UserDataInfo?,
	isInternetAvailable: Boolean,
	cacheOperation: (CachingReason) -> Unit,
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
