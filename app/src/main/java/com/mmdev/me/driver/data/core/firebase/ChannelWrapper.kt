/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.09.2020 19:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.firebase

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.SendChannel

/**
 * Check if the channel is not closed and try to emit a value, catching [CancellationException]
 * if the corresponding has been cancelled.
 * This extension is used in callbackFlow.
 */

fun <E> SendChannel<E>.safeOffer(value: E): Boolean {
	if (isClosedForSend) return false
	return try {
		offer(value)
	} catch (e: CancellationException) {
		false
	}
}