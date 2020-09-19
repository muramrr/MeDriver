/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.cioccarellia.ksprefs.KsPrefs
import com.mmdev.me.driver.core.di.*
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.Language.UKRAINIAN
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.core.utils.log.DebugConfig
import com.mmdev.me.driver.core.utils.log.MyLogger
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logInfo
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
		private const val LANGUAGE_KEY = "language"
		
		private val prefs: KsPrefs by inject(KsPrefs::class.java)
		
		//make-public, because based on this values ui setup init values
		var isLightMode: Boolean = true
		var metricSystem: MetricSystem = KILOMETERS
		var appLanguage: Language = UKRAINIAN
		
		
		fun toggleMetricSystem(metricSystem: MetricSystem) {
			prefs.push(METRIC_SYSTEM_KEY, metricSystem)
			this.metricSystem = metricSystem
			logDebug(TAG, "Metric system changed.")
		}
		
		fun toggleThemeMode(themeMode: ThemeMode) {
			isLightMode = themeMode == LIGHT_MODE
			prefs.push(THEME_MODE_KEY, themeMode)
			logDebug(TAG, "AppTheme changed.")
			ThemeHelper.applyTheme(themeMode)
		}
		
		fun changeLanguage(language: Language) {
			prefs.push(LANGUAGE_KEY, language)
			appLanguage = language
			logDebug(TAG, "Language changed.")
		}
		
		
		
		@Volatile
		var debug: DebugConfig = DebugConfig.Default

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
				// The lines on which modules are written represents
				// some kind of "layers" inside dependencies.
				listOf(
					ViewModelsModule,
					RepositoryModule,
					DataSourceRemoteModule, DataSourceLocalModule,
					NetworkModule, FirebaseModule, DatabaseModule, PreferencesModule
				)
			)
			koin.createRootScope()
		}
		
		
		applyInitThemeMode()
		applyInitMetricSystem()
		applyInitLanguage()
		
		super.onCreate()
		logInfo(TAG, "isLightMode on? -$isLightMode")
		logInfo(TAG, "current metric system - ${metricSystem.name}")
		logInfo(TAG, "Application language - ${appLanguage.name}")
	}
	
	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(LocaleHelper.newLocationContext(base, appLanguage))
	}
	
	/**
	 * When user decides to change system device language it will override application settings
	 * so its a fix
	 */
	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		LocaleHelper.overrideLocale(this, appLanguage)
	}
	
	//called only on app startup to pull saved value
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
	
	//called only on app startup to pull saved value
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
	
	//called only on app startup to pull saved value
	private fun applyInitLanguage() {
		if (prefs.exists(LANGUAGE_KEY)) {
			prefs.pull<Language>(LANGUAGE_KEY).also{ appLanguage = it }
		}
		//if not exists - apply UKRAINIAN language as app default
		else {
			with(UKRAINIAN) {
				prefs.push(LANGUAGE_KEY, this)
				appLanguage = this
			}
		}
	}
}