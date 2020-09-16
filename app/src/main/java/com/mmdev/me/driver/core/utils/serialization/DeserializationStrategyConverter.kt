/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 02:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.serialization

import kotlinx.serialization.DeserializationStrategy
import okhttp3.ResponseBody
import retrofit2.Converter


/* reference to [https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter] */


internal class DeserializationStrategyConverter<T>(
	private val loader: DeserializationStrategy<T>,
	private val serializer: Serializer
) : Converter<ResponseBody, T> {
	
	override fun convert(value: ResponseBody) = serializer.fromResponseBody(loader, value)
	
}