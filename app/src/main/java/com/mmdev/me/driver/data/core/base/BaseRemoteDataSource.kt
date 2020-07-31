/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 20:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.base

import com.mmdev.me.driver.data.core.ResponseState

/**
 *
 */

open class BaseRemoteDataSource {

	suspend fun <T: Any> safeCallResponse(call: suspend ()-> T,
	                                      errorMessage: String) : ResponseState<T> =
		try {
			val callResult = call.invoke()
			ResponseState.Success(callResult)
		}
		catch (t: Throwable) {
			ResponseState.Error(errorMessage, t)
		}
}