/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 18:26
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Dispatchers wrapper class
 */

object MyDispatchers {

	fun io(): CoroutineDispatcher = Dispatchers.IO
	fun default(): CoroutineDispatcher = Dispatchers.Default
	fun main(): CoroutineDispatcher = Dispatchers.Main
	fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined

}