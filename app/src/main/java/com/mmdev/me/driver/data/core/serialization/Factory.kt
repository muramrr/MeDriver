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