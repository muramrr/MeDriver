/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.08.2020 19:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.serialization.asConverterFactory
import com.mmdev.me.driver.data.datasource.remote.api.FuelApi
import com.mmdev.me.driver.data.datasource.remote.api.VINCodeApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Interceptor.Companion.invoke
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit


private const val BASE_URL = "https://localhost"
private val contentType = "application/json".toMediaType()

val NetworkModule = module {


	single { provideRetrofit() }

	single { provideVINCodeApi(retrofit = get()) }
	single { provideFuelApi(retrofit = get()) }



}

fun provideRetrofit(): Retrofit = Retrofit.Builder()
	.apply {
		addConverterFactory(Json{ ignoreUnknownKeys = true }.asConverterFactory(contentType))
		baseUrl(BASE_URL)
		if (MedriverApp.debug.enabled) client(okHttpClient)
	}
	.build()

private val okHttpClient: OkHttpClient
	get() = OkHttpClient.Builder()
		.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
		.addInterceptor(baseInterceptor)
		.build()

private val baseInterceptor: Interceptor = invoke { chain ->
	val newUrl = chain
		.request()
		.url
		.newBuilder()
		.build()

	val request = chain
		.request()
		.newBuilder()
		.url(newUrl)
		.build()

	return@invoke chain.proceed(request)
}


fun provideVINCodeApi(retrofit: Retrofit): VINCodeApi = retrofit.create(VINCodeApi::class.java)

fun provideFuelApi(retrofit: Retrofit): FuelApi = retrofit.create(FuelApi::class.java)