/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.core.serialization.asConverterFactory
import com.mmdev.me.driver.data.datasource.fuel.prices.api.retrofit.FuelApi
import com.mmdev.me.driver.data.datasource.vin.api.retrofit.VinCodeApi
import com.mmdev.me.driver.data.sync.download.DataDownloader
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
	
	single { DataDownloader(vehicles = get(), maintenance = get(), fuelHistory = get(), journal = get()) }

	single { provideRetrofit() }
	
	single { provideVinCodeApi(retrofit = get()) }
	single { provideFuelApi(retrofit = get()) }
	
}

private fun provideVinCodeApi(retrofit: Retrofit): VinCodeApi = retrofit.create(VinCodeApi::class.java)
private fun provideFuelApi(retrofit: Retrofit): FuelApi = retrofit.create(FuelApi::class.java)

private fun provideRetrofit(): Retrofit = Retrofit.Builder()
	.apply {
		addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory(contentType))
		baseUrl(BASE_URL)
		if (MedriverApp.debug.isEnabled) client(okHttpClient)
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


