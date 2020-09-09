/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.09.2020 20:24
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
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.MyLogger
import com.mmdev.me.driver.core.utils.ThemeHelper
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.core.utils.logInfo
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
		private const val TAG = "mylogs_MEDRIVERAPP"
		private const val THEME_MODE_KEY = "theme_mode"
		private const val METRIC_SYSTEM_KEY = "metric_system"
		
		private val prefs: KsPrefs by inject(KsPrefs::class.java)
		
		internal var isLightMode: Boolean = true
		internal var metricSystem: MetricSystem = KILOMETERS
		
		internal fun toggleMetricSystem(metricSystem: MetricSystem) {
			prefs.push(METRIC_SYSTEM_KEY, metricSystem)
			this.metricSystem = metricSystem
			logInfo(TAG, "current metric system - ${metricSystem.name}")
		}
		
		internal fun toggleThemeMode(themeMode: ThemeMode) {
			isLightMode = themeMode == LIGHT_MODE
			prefs.push(THEME_MODE_KEY, themeMode)
			logInfo(TAG, "isLightMode on? -$isLightMode")
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
		
		
		applyInitThemeMode()
		applyInitMetricSystem()
		
		super.onCreate()
	}
	
	
	//called only on app startup
	private fun applyInitThemeMode() {
		if (prefs.exists(THEME_MODE_KEY)){
			prefs.pull<ThemeMode>(THEME_MODE_KEY).also{
				ThemeHelper.applyTheme(it)
				isLightMode = it == LIGHT_MODE
			}
		}
		//if not exists - apply LIGHT_MODE as default and save
		else {
			with(LIGHT_MODE) {
				prefs.push(THEME_MODE_KEY, this)
				ThemeHelper.applyTheme(this)
				isLightMode = true
			}
		}
	}
	
	//called only on app startup
	private fun applyInitMetricSystem() {
		if (prefs.exists(METRIC_SYSTEM_KEY)){
			prefs.pull<MetricSystem>(METRIC_SYSTEM_KEY).also{ metricSystem = it }
		}
		//if not exists - apply KILOMETERS as default metric system and save
		else {
			with(KILOMETERS) {
				prefs.push(METRIC_SYSTEM_KEY, this)
				metricSystem = this
			}
		}
	}
}