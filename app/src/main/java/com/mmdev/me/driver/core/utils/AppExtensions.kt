/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 19:22
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils

import com.mmdev.me.driver.core.MedriverApp

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
