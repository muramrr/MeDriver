/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 19:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.FuelPricesRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.IFuelPricesRemoteDataSource
import com.mmdev.me.driver.data.datasource.user.auth.AuthCollector
import com.mmdev.me.driver.data.datasource.user.auth.FirebaseAuthDataSourceImpl
import com.mmdev.me.driver.data.datasource.user.auth.IFirebaseAuthDataSource
import com.mmdev.me.driver.data.datasource.user.remote.IUserRemoteDataSource
import com.mmdev.me.driver.data.datasource.user.remote.UserRemoteDataSourceImpl
import com.mmdev.me.driver.data.datasource.vin.remote.IVINRemoteDataSource
import com.mmdev.me.driver.data.datasource.vin.remote.VINRemoteDataSourceImpl
import org.koin.dsl.module


/**
 * [DataSourceRemoteModule] provides RemoteDataSource instances
 */

val DataSourceRemoteModule = module {
	
	single { AuthCollector(auth = FirebaseAuth.getInstance()) }
	
	single<IVINRemoteDataSource> { VINRemoteDataSourceImpl(vinCodeApi = get()) }
	single<IFuelPricesRemoteDataSource> { FuelPricesRemoteDataSourceImpl(fuelApi = get()) }
	
	single<IUserRemoteDataSource> { UserRemoteDataSourceImpl(db = FirebaseFirestore.getInstance()) }
	single<IFirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(auth = FirebaseAuth.getInstance()) }
	
	
}