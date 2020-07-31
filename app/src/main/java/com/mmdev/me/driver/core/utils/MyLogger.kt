/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 16:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils

import android.util.Log
import com.mmdev.me.driver.core.utils.MyLogger.Default

/**
 * May be used to create a custom logging solution to override the [Default] behaviour.
 */
interface MyLogger {

	/**
	 * @param tag used to identify the source of a log message.
	 * @param message the message to be logged.
	 */
	fun log(tag: String, message: String)

	/**
	 * Debug implementation of [MyLogger].
	 */
	object Debug : MyLogger {
		override fun log(tag: String, message: String) {
			Log.wtf(tag, message)
		}
	}

	/**
	 * Default implementation of [MyLogger].
	 * No messages to Logcat
	 */
	object Default : MyLogger {
		override fun log(tag: String, message: String) {}
	}
}