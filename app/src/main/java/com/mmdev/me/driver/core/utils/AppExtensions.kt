/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.08.2020 19:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils

import com.mmdev.me.driver.core.MedriverApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.math.round

/**
 *
 */


internal fun logWarn(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logWarn(tag, message)

internal fun logError(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logError(tag, message)

internal fun logDebug(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logDebug(tag, message)

internal fun logInfo(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logInfo(tag, message)

internal fun logWtf(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logWtf(tag, message)


// @see https://stackoverflow.com/questions/58658630/parallel-request-with-retrofit-coroutines-and-suspend-functions
// Flow.toMap()
suspend fun <K, V> Flow<Pair<K, V>>.toMap(): Map<K, V> {
	val result = mutableMapOf<K, V>()
	collect { (k, v) -> result[k] = v }
	return result
}


fun Double.roundTo(decimals: Int): Double {
	var multiplier = 1.0
	repeat(decimals) { multiplier *= 10 }
	return round(this * multiplier) / multiplier
}

