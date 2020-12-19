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

package com.mmdev.me.driver.data.core.base.datasource

import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * Base class for any datasource class
 * Primary usage for datasource is wrapping (server or local database) get/set operations
 */

abstract class BaseDataSource {
	
	protected val TAG = "mylogs_${javaClass.simpleName}"

	protected suspend inline fun <T> safeCall(TAG: String, call: suspend () -> T?): SimpleResult<T> =
		try {
			val result = call.invoke()
			if (result != null) ResultState.success(result)
			else ResultState.failure(NullPointerException("Data is null"))
			
		}
		catch (t: Throwable) {
			logError(TAG, "${t.message}")
			ResultState.Failure(t)
		}
	
}