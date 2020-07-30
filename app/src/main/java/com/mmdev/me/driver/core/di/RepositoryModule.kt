package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.remote.IVINDataSource
import com.mmdev.me.driver.data.repository.VINRepositoryImpl
import com.mmdev.me.driver.domain.vin.IVINRepository
import org.koin.dsl.module

/**
 * This is the documentation block about the class
 */


val RepositoryModule = module {

	single { provideVINRepository(_vinDataSource = get())}

}


fun provideVINRepository(_vinDataSource: IVINDataSource): IVINRepository =
	VINRepositoryImpl(_vinDataSource)