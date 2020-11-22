/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 15:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * time simplified expressions
 */

fun Instant.toCurrentTimeAndDate(): LocalDateTime = toLocalDateTime(TimeZone.currentSystemDefault())
fun convertToLocalDateTime(timeInMillis: Long): LocalDateTime = Instant.fromEpochMilliseconds(timeInMillis).toCurrentTimeAndDate()
fun currentEpochTime(): Long = Clock.System.now().toEpochMilliseconds()
fun currentTimeAndDate(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())