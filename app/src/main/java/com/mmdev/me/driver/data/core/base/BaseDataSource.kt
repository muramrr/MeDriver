/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.09.2020 21:10
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.base

import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult



open class BaseDataSource {
	
	
	protected val TAG = "mylogs_${javaClass.simpleName}"

	suspend fun <T> safeCall(call: suspend ()-> T) : SimpleResult<T> =
		try {
			val result = call.invoke()
			if (result != null) ResultState.success(result)
			else {
				logWarn(TAG, "Safe call returns null")
				ResultState.failure(NullPointerException("Data is null"))
			}
		}
		catch (t: Throwable) {
			logError(TAG, "${t.message}")
			ResultState.Failure(t)
		}
}