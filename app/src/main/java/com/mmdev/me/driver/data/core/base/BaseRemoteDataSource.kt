/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 16:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.base

import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult



open class BaseRemoteDataSource {

	suspend fun <T> safeCallResponse(call: suspend ()-> T) : SimpleResult<T> =
		try {
			val callResult = call.invoke()
			ResultState.Success(callResult)
		}
		catch (t: Throwable) {
			ResultState.Failure(t)
		}
}