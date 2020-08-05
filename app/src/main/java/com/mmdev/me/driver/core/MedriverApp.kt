/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 16:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core

import android.app.Application
import com.mmdev.me.driver.core.di.*
import com.mmdev.me.driver.core.utils.DebugConfig
import com.mmdev.me.driver.core.utils.MyLogger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Contains dependency modules
 * From top to bottom representing DI tree
 * For example: [ViewModelsModule] depends on [RepositoryModule]
 * [RepositoryModule] depends on [DataSourceModule]
 * and so on.
 */

class MedriverApp : Application() {

	companion object {
		@Volatile
		internal var debug: DebugConfig = DebugConfig.Default

		/**
		 * Enable or disable [Application] debug mode.
		 * enabled by default
		 *
		 * @param enabled enable the debug mode.
		 * @param logger logging implementation.
		 */
		fun debugMode(enabled: Boolean, logger: MyLogger) {
			debug = object: DebugConfig {
				override val enabled = enabled
				override val logger = logger
			}
		}
	}

	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidContext(this@MedriverApp)
			if (debug.enabled) androidLogger()
			modules(listOf(
					ViewModelsModule,
					RepositoryModule,
					DataSourceModule,
					NetworkModule, DatabaseModule
			))
		}
	}
}