/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.09.2020 16:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.log

import com.mmdev.me.driver.BuildConfig

interface DebugConfig {

	val isEnabled: Boolean
	val logger: MyLogger

	object Default : DebugConfig {
		override val isEnabled: Boolean = BuildConfig.DEBUG
		override val logger: MyLogger = if (isEnabled) MyLogger.Debug else MyLogger.Default
	}
}