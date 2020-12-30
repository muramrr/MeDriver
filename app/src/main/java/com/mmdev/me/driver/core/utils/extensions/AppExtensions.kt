/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.core.utils.extensions

import android.content.Context
import android.provider.Settings.Secure
import androidx.lifecycle.LifecycleOwner
import com.mmdev.me.driver.core.utils.FlowObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect


// @see https://stackoverflow.com/questions/58658630/parallel-request-with-retrofit-coroutines-and-suspend-functions
// Flow.toMap()
suspend fun <K, V> Flow<Pair<K, V>>.toMap(): Map<K, V> {
	val result = mutableMapOf<K, V>()
	collect { (k, v) -> result[k] = v }
	return result
}

inline fun <reified T> Flow<T>.observe(
	lifecycleOwner: LifecycleOwner,
	noinline collector: suspend (T) -> Unit
) {
	FlowObserver(lifecycleOwner, this, collector)
}

inline fun <reified T> Flow<T>.observeIn(
	lifecycleOwner: LifecycleOwner
) {
	FlowObserver(lifecycleOwner, this, {})
}


/**
 * Gets the hardware serial number of this device.
 *
 * @return serial number or Settings.Secure.ANDROID_ID if not available.
 * Credit: SecurePreferences for Android
 */
fun getAndroidId(context: Context): String = Secure.getString(context.contentResolver, Secure.ANDROID_ID)