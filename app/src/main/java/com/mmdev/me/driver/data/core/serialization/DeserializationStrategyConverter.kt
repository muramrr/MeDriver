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

import kotlinx.serialization.DeserializationStrategy
import okhttp3.ResponseBody
import retrofit2.Converter


/* reference to [https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter] */


class DeserializationStrategyConverter<T>(
	private val loader: DeserializationStrategy<T>,
	private val serializer: Serializer
) : Converter<ResponseBody, T> {
	
	override fun convert(value: ResponseBody) = serializer.fromResponseBody(loader, value)
	
}