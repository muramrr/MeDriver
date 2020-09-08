/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.09.2020 18:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core

import android.app.Application
import com.cioccarellia.ksprefs.KsPrefs
import com.mmdev.me.driver.core.di.DataSourceLocalModule
import com.mmdev.me.driver.core.di.DataSourceRemoteModule
import com.mmdev.me.driver.core.di.DatabaseModule
import com.mmdev.me.driver.core.di.NetworkModule
import com.mmdev.me.driver.core.di.PreferencesModule
import com.mmdev.me.driver.core.di.RepositoryModule
import com.mmdev.me.driver.core.di.ViewModelsModule
import com.mmdev.me.driver.core.utils.DebugConfig
import com.mmdev.me.driver.core.utils.MyLogger
import com.mmdev.me.driver.core.utils.ThemeHelper
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.LIGHT_MODE
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

/**
 * Contains dependency modules
 * From top to bottom representing DI tree
 * For example: [ViewModelsModule] depends on [RepositoryModule]
 * [RepositoryModule] depends on [DataSourceLocalModule] and [DataSourceRemoteModule]
 * and so on.
 */

class MedriverApp : Application() {
	
	
	
	companion object {
		private val prefs: KsPrefs by inject(KsPrefs::class.java)
		
		internal var isLightMode: Boolean = true
		
		internal fun toggleThemeMode(themeMode: ThemeMode) {
			isLightMode = themeMode == LIGHT_MODE
			prefs.push("key", themeMode)
			ThemeHelper.applyTheme(themeMode)
		}
		
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
	
		//initKoin
		startKoin {
			androidContext(this@MedriverApp)
			if (debug.enabled) androidLogger()
			koin.loadModules(
				listOf(
					ViewModelsModule,
					RepositoryModule,
					DataSourceRemoteModule, DataSourceLocalModule,
					NetworkModule, DatabaseModule,
					PreferencesModule
				)
			)
			koin.createRootScope()
		}
		
		
		applyThemeMode()
		
		
		super.onCreate()
	}
	
	
	
	private fun applyThemeMode() {
		if (prefs.exists("key")){
			prefs.pull<ThemeMode>("key").also{
				ThemeHelper.applyTheme(it)
				isLightMode = it == LIGHT_MODE
			}
		}
		//if not exists - apply default light theme
		else {
			with(LIGHT_MODE) {
				prefs.push("key", this)
				ThemeHelper.applyTheme(this)
				isLightMode = true
			}
		}
	}
}