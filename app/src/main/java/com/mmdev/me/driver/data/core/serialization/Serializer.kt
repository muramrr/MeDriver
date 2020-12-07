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

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody


/* reference to [https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter] */


sealed class Serializer {
	
	
	abstract fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T
	abstract fun <T> toRequestBody(
		contentType: MediaType, saver: SerializationStrategy<T>, value: T
	): RequestBody
	
	
	
	
	class FromString(private val format: StringFormat) : Serializer() {
		
		override fun <T> fromResponseBody(
			loader: DeserializationStrategy<T>,
			body: ResponseBody
		): T = format.decodeFromString(loader, body.string())
		
		override fun <T> toRequestBody(
			contentType: MediaType,
			saver: SerializationStrategy<T>,
			value: T
		): RequestBody {
			val string = format.encodeToString(saver, value)
			return string.toRequestBody(contentType)
		}
		
	}
	
	class FromBytes(private val format: BinaryFormat): Serializer() {
		
		override fun <T> fromResponseBody(
			loader: DeserializationStrategy<T>,
			body: ResponseBody
		): T = format.decodeFromByteArray(loader, body.bytes())
		
		override fun <T> toRequestBody(
			contentType: MediaType,
			saver: SerializationStrategy<T>,
			value: T
		): RequestBody {
			val bytes = format.encodeToByteArray(saver, value)
			return bytes.toRequestBody(contentType, 0, bytes.size)
		}
		
	}
}