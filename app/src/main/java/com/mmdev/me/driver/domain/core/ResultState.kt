/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 16:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.core

/**
 * Primary used to define bounds between network and ui states
 */

typealias SimpleResult<T> = ResultState<T, Throwable>

sealed class ResultState<out T, out E> {

	data class Success<out T>(val data: T) : ResultState<T, Nothing>()

	data class Failure<out E>(val error: E) : ResultState<Nothing, E>()
	
	inline fun <C> fold(success: (T) -> C,
	                    failure: (E) -> C): C =
		when (this) {
			is Success -> success(data)
			is Failure -> failure(error)
		}

}