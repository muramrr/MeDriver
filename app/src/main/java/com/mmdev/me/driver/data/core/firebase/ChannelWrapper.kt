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