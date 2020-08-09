/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 18:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.mappers


// Non-nullable to Non-nullable
inline fun <I, O> mapList(input: List<I>, mapSingle: (I) -> O): List<O> {
	return input.map { mapSingle(it) }
}

// Nullable to Non-nullable
inline fun <I, O> mapNullInputList(input: List<I>?, mapSingle: (I) -> O): List<O> {
	return input?.map { mapSingle(it) } ?: emptyList()
}

// Non-nullable to Nullable
inline fun <I, O> mapNullOutputList(input: List<I>, mapSingle: (I) -> O): List<O>? {
	return if (input.isEmpty()) null else input.map { mapSingle(it) }
}