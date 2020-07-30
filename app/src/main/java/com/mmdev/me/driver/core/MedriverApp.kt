/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.07.20 21:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core

import android.app.Application
import com.mmdev.me.driver.core.di.DataSourceModule
import com.mmdev.me.driver.core.di.NetworkModule
import com.mmdev.me.driver.core.di.RepositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 *
 */

class MedriverApp : Application() {


	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidContext(this@MedriverApp)
			androidLogger()
			modules(listOf(
					NetworkModule,
					DataSourceModule,
					RepositoryModule
			))
		}
	}
}