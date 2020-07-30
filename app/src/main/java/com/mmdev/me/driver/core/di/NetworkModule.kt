package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.api.VINCodeApi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val BASE_URL = ""

val NetworkModule = module {


	single { provideRetrofit() }

	single { provideVINCodeApi(retrofit = get()) }



}

fun provideRetrofit(): Retrofit = Retrofit.Builder()
	.baseUrl(BASE_URL)
	.addConverterFactory(GsonConverterFactory.create())
	.build()


fun provideVINCodeApi(retrofit: Retrofit): VINCodeApi = retrofit.create(VINCodeApi::class.java)