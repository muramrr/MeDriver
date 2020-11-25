/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.11.2020 21:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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