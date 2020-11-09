/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.11.2020 15:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.log

import com.mmdev.me.driver.core.MedriverApp


fun logWarn(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logWarn(tag, message)
fun logWarn(clazz: Class<Any>, message: String) =
	logWarn("mylogs_${clazz.simpleName}", message)



fun logError(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logError(tag, message)
fun logError(clazz: Class<Any>, message: String) =
	logError("mylogs_${clazz.simpleName}", message)



fun logDebug(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logDebug(tag, message)
fun logDebug(clazz: Class<Any>, message: String) =
	logDebug("mylogs_${clazz.simpleName}", message)



fun logInfo(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logInfo(tag, message)
fun logInfo(clazz: Class<Any>, message: String) =
	logInfo("mylogs_${clazz.simpleName}", message)



fun logWtf(tag: String = "mylogs", message: String) =
	MedriverApp.debug.logger.logWtf(tag, message)
fun logWtf(clazz: Class<Any>, message: String) =
	logWtf("mylogs_${clazz.simpleName}", message)




