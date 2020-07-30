package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.api.VINCodeApi
import com.mmdev.me.driver.data.remote.IVINDataSource
import com.mmdev.me.driver.data.remote.VINDataSourceImpl
import org.koin.dsl.module


/**
 * This is the documentation block about the class
 */

val DataSourceModule = module {

	single { provideVinDataSource(vinCodeApi = get()) }


}

fun provideVinDataSource(vinCodeApi: VINCodeApi) : IVINDataSource = VINDataSourceImpl(vinCodeApi)