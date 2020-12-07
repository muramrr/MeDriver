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

import kotlinx.serialization.SerializationStrategy
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter


/* reference to [https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter] */


class SerializationStrategyConverter<T>(
	private val contentType: MediaType,
	private val saver: SerializationStrategy<T>,
	private val serializer: Serializer
) : Converter<T, RequestBody> {
	override fun convert(value: T) = serializer.toRequestBody(contentType, saver, value)
}