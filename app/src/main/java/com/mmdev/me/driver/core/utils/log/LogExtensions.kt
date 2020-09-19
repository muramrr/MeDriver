/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.log

import com.mmdev.me.driver.core.MedriverApp


fun logWarn(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logWarn(tag, message)

fun logError(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logError(tag, message)

fun logDebug(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logDebug(tag, message)

fun logInfo(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logInfo(tag, message)

fun logWtf(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logWtf(tag, message)

