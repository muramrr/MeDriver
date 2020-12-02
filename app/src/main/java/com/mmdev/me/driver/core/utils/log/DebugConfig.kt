/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.12.2020 15:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.log

import com.mmdev.me.driver.BuildConfig

/**
 * Own solution for logging operations
 * Good enough to not use Timber or any other third-party loggers
 */

interface DebugConfig {

	val isEnabled: Boolean
	val logger: MyLogger

	object Default : DebugConfig {
		override val isEnabled: Boolean = BuildConfig.DEBUG
		override val logger: MyLogger = if (isEnabled) MyLogger.Debug else MyLogger.Default
	}
	
	object Enabled : DebugConfig {
		override val isEnabled: Boolean = true
		override val logger: MyLogger = MyLogger.Debug
	}
}