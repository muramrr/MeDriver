/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 16:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.extensions

import android.content.Context
import android.provider.Settings.Secure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect


// @see https://stackoverflow.com/questions/58658630/parallel-request-with-retrofit-coroutines-and-suspend-functions
// Flow.toMap()
suspend fun <K, V> Flow<Pair<K, V>>.toMap(): Map<K, V> {
	val result = mutableMapOf<K, V>()
	collect { (k, v) -> result[k] = v }
	return result
}


/**
 * Gets the hardware serial number of this device.
 *
 * @return serial number or Settings.Secure.ANDROID_ID if not available.
 * Credit: SecurePreferences for Android
 */
fun getAndroidId(context: Context): String = Secure.getString(context.contentResolver, Secure.ANDROID_ID)