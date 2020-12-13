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

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.core.serialization.asConverterFactory
import com.mmdev.me.driver.data.datasource.billing.TimeApi
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
	single { provideTimeApi(retrofit = get()) }
	
}

private fun provideVinCodeApi(retrofit: Retrofit): VinCodeApi = retrofit.create(VinCodeApi::class.java)
private fun provideFuelApi(retrofit: Retrofit): FuelApi = retrofit.create(FuelApi::class.java)
private fun provideTimeApi(retrofit: Retrofit): TimeApi = retrofit.create(TimeApi::class.java)

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


