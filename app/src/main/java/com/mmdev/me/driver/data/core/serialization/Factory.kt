/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.11.2020 21:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.serialization

import com.mmdev.me.driver.data.core.serialization.Serializer.*
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/* reference to [https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter] */


class Factory(
	private val contentType: MediaType,
	private val serializer: Serializer
): Converter.Factory() {
	
	
	override fun responseBodyConverter(
		type: Type,
		annotations: Array<out Annotation>,
		retrofit: Retrofit
	): Converter<ResponseBody, *>? =
		DeserializationStrategyConverter(serializer(type), serializer)
	
	override fun requestBodyConverter(
		type: Type,
		parameterAnnotations: Array<out Annotation>,
		methodAnnotations: Array<out Annotation>,
		retrofit: Retrofit
	): Converter<*, RequestBody>? =
		SerializationStrategyConverter(contentType, serializer(type), serializer)
}



/**
 * Return a [Converter.Factory] which uses Kotlin serialization for string-based payloads.
 *
 * Because Kotlin serialization is so flexible in the types it supports, this converter assumes
 * that it can handle all types. If you are mixing this with something else, you must add this
 * instance last to allow the other converters a chance to see their types.
 */
fun StringFormat.asConverterFactory(contentType: MediaType): Converter.Factory =
	Factory(contentType, FromString(this))


/**
 * Return a [Converter.Factory] which uses Kotlin serialization for byte-based payloads.
 *
 * Because Kotlin serialization is so flexible in the types it supports, this converter assumes
 * that it can handle all types. If you are mixing this with something else, you must add this
 * instance last to allow the other converters a chance to see their types.
 */
fun BinaryFormat.asConverterFactory(contentType: MediaType): Converter.Factory =
	Factory(contentType, FromBytes(this))