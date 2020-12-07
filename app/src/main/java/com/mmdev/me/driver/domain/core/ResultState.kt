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

fun combineResultStates(
	first: SimpleResult<Unit>,
	second: SimpleResult<Unit>
): SimpleResult<Unit> = when {
	first is ResultState.Failure -> ResultState.failure(first.error)
	second is ResultState.Failure -> ResultState.failure(second.error)
	else -> ResultState.success(Unit)
}