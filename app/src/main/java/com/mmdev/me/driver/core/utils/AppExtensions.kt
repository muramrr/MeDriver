/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.10.2020 16:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochMilliseconds
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import kotlin.math.round


// @see https://stackoverflow.com/questions/58658630/parallel-request-with-retrofit-coroutines-and-suspend-functions
// Flow.toMap()
suspend fun <K, V> Flow<Pair<K, V>>.toMap(): Map<K, V> {
	val result = mutableMapOf<K, V>()
	collect { (k, v) -> result[k] = v }
	return result
}


fun Float.roundTo(decimals: Int): Float {
	var multiplier = 1.0
	repeat(decimals) { multiplier *= 10 }
	return (round(this * multiplier) / multiplier).toFloat()
}

fun Double.roundTo(decimals: Int): Double {
	var multiplier = 1.0
	repeat(decimals) { multiplier *= 10 }
	return round(this * multiplier) / multiplier
}

//time simplified expressions
fun Instant.toCurrentTimeAndDate(): LocalDateTime = toLocalDateTime(currentSystemDefault())
fun convertToLocalDateTime(timeInMillis: Long): LocalDateTime = fromEpochMilliseconds(timeInMillis).toCurrentTimeAndDate()
fun currentEpochTime(): Long = Clock.System.now().toEpochMilliseconds()
fun currentTimeAndDate() = Clock.System.now().toLocalDateTime(currentSystemDefault())