/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 01:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.log

import android.util.Log
import com.mmdev.me.driver.core.utils.log.MyLogger.Default

/**
 * May be used to create a custom logging solution to override the [Default] behaviour.
 */

interface MyLogger {

	/**
	 * @param tag used to identify the source of a log message.
	 * @param message the message to be logged.
	 */
	fun logWarn(tag: String, message: String)

	fun logError(tag: String, message: String)

	fun logDebug(tag: String, message: String)

	fun logInfo(tag: String, message: String)

	fun logWtf(tag: String, message: String)

	/**
	 * Debug implementation of [MyLogger].
	 */
	object Debug : MyLogger {

		override fun logWarn(tag: String, message: String) {
			Log.w(tag, message)
		}

		override fun logError(tag: String, message: String) {
			Log.e(tag, message)
		}

		override fun logDebug(tag: String, message: String) {
			Log.d(tag, message)
		}

		override fun logInfo(tag: String, message: String) {
			Log.i(tag, message)
		}

		override fun logWtf(tag: String, message: String) {
			Log.wtf(tag, message)
		}
	}

	/**
	 * Default implementation of [MyLogger].
	 * No messages to Logcat
	 */
	object Default : MyLogger {
		override fun logWarn(tag: String, message: String) {}
		override fun logError(tag: String, message: String) {}
		override fun logDebug(tag: String, message: String) {}
		override fun logInfo(tag: String, message: String) {}
		override fun logWtf(tag: String, message: String) {}
	}
}