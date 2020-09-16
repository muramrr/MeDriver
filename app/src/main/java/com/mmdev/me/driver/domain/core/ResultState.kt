/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.09.2020 00:57
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

	data class Success<out T>(val data: T) :  ResultState<T, Nothing>()

	data class Failure<out E>(val error: E) : ResultState<Nothing, E>()
	
	companion object {
		fun <T> success(data: T) = Success(data)
		fun <E> failure(error: E) = Failure(error)
		fun <T, E> ResultState<T, E>.toUnit() : SimpleResult<Unit> =
			this.fold(
				success = { success(Unit) },
				failure = { failure(if (it is Throwable) it else Exception()) }
			)
	}
	
	inline fun <C> fold(success: (T) -> C,
	                    failure: (E) -> C): C =
		when (this) {
			is Success -> success(data)
			is Failure -> failure(error)
		}

}