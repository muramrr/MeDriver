/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.09.2020 17:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.log

import com.mmdev.me.driver.BuildConfig

internal interface DebugConfig {

	val enabled: Boolean
	val logger: MyLogger

	object Default : DebugConfig {
		override val enabled: Boolean = BuildConfig.DEBUG
		override val logger: MyLogger = if (enabled) MyLogger.Debug else MyLogger.Default
	}
}