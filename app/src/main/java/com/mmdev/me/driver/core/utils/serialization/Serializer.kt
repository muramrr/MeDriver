/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.08.2020 20:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.serialization

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

internal sealed class Serializer {
	abstract fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T
	abstract fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody
	
	class FromString(private val format: StringFormat) : Serializer() {
		override fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T {
			val string = body.string()
			return format.decodeFromString(loader, string)
		}
		
		override fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody {
			val string = format.encodeToString(saver, value)
			return string.toRequestBody(contentType)
		}
	}
	
	class FromBytes(private val format: BinaryFormat): Serializer() {
		override fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T {
			val bytes = body.bytes()
			return format.decodeFromByteArray(loader, bytes)
		}
		
		override fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody {
			val bytes = format.encodeToByteArray(saver, value)
			return bytes.toRequestBody(contentType, 0, bytes.size)
		}
	}
}