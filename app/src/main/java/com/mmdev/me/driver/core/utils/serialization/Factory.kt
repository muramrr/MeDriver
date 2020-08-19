/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.08.2020 18:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.serialization

import com.mmdev.me.driver.core.utils.serialization.Serializer.FromBytes
import com.mmdev.me.driver.core.utils.serialization.Serializer.FromString
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class Factory(
	private val contentType: MediaType,
	private val serializer: Serializer
): Converter.Factory() {
	override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>,
	                                   retrofit: Retrofit): Converter<ResponseBody, *>? {
		val loader = serializer(type)
		return DeserializationStrategyConverter(loader, serializer)
	}
	
	override fun requestBodyConverter(type: Type, parameterAnnotations: Array<out Annotation>,
	                                  methodAnnotations: Array<out Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
		val saver = serializer(type)
		return SerializationStrategyConverter(contentType, saver, serializer)
	}
}

/**
 * Return a [Converter.Factory] which uses Kotlin serialization for string-based payloads.
 *
 * Because Kotlin serialization is so flexible in the types it supports, this converter assumes
 * that it can handle all types. If you are mixing this with something else, you must add this
 * instance last to allow the other converters a chance to see their types.
 */
fun StringFormat.asConverterFactory(contentType: MediaType): Converter.Factory {
	return Factory(contentType, FromString(this))
}

/**
 * Return a [Converter.Factory] which uses Kotlin serialization for byte-based payloads.
 *
 * Because Kotlin serialization is so flexible in the types it supports, this converter assumes
 * that it can handle all types. If you are mixing this with something else, you must add this
 * instance last to allow the other converters a chance to see their types.
 */
fun BinaryFormat.asConverterFactory(contentType: MediaType): Converter.Factory {
	return Factory(contentType, FromBytes(this))
}