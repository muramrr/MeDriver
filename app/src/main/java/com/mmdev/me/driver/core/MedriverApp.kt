/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 20:02
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
import com.mmdev.me.driver.core.di.DataSourceLocalModule
import com.mmdev.me.driver.core.di.DataSourceRemoteModule
import com.mmdev.me.driver.core.di.DatabaseModule
import com.mmdev.me.driver.core.di.FirebaseModule
import com.mmdev.me.driver.core.di.NetworkModule
import com.mmdev.me.driver.core.di.RepositoryModule
import com.mmdev.me.driver.core.di.ViewModelsModule
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.Language.ENGLISH
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
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

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
		
		private const val PREFERENCES_NAME = "settings"
		
		private const val THEME_MODE_KEY = "theme_mode"
		private const val METRIC_SYSTEM_KEY = "metric_system"
		private const val LANGUAGE_KEY = "language"
		private const val VEHICLE_VIN_CODE_KEY = "vehicle_vin"
		//private const val USER_EMAIL_KEY = "user_email"
		
		private lateinit var appContext: Context
		val prefs by lazy {
			KsPrefs(appContext, PREFERENCES_NAME) {
				//	encryptionType = EncryptionType.KeyStore("key")
			}
		}
		
		// public because the initial UI is built from these values
		// and some of these values are used across application
		
		var isLightMode: Boolean = true
			private set
		var metricSystem: MetricSystem = KILOMETERS
			private set
		@Volatile
		var appLanguage: Language = ENGLISH
			private set
		@Volatile
		var currentUser: UserModel? = null
			@Synchronized set
		
		var currentVehicleVinCode: String = ""
			private set
		@Volatile
		var currentVehicle: Vehicle? = null
			@Synchronized set
		
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
		
		fun changeCurrentVinCode(vin: String) {
			prefs.push(VEHICLE_VIN_CODE_KEY, vin)
			currentVehicleVinCode = vin
			logDebug(TAG, "Vehicle changed.")
		}
		
//		fun changeCurrentUserEmail(email: String) {
//			prefs.push(USER_EMAIL_KEY, email)
//			userEmail = email
//			logDebug(TAG, "Email changed.")
//		}
		
		
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
		
		appContext = applicationContext
		
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
					NetworkModule, FirebaseModule, DatabaseModule
				)
			)
			koin.createRootScope()
		}
		
		
		applyInitialThemeMode()
		applyInitialMetricSystem()
		applyInitialLanguage()
		applyInitialVinCode()
		
		
		
		super.onCreate()
		logInfo(TAG, "isLightMode on? -$isLightMode")
		logInfo(TAG, "current metric system - ${metricSystem.name}")
		logInfo(TAG, "Application language - ${appLanguage.name}")
		logInfo(TAG, "Current vehicle - $currentVehicle")
	}
	
	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(LocaleHelper.newLocaleContext(base, appLanguage))
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
	private fun applyInitialThemeMode() {
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
	private fun applyInitialMetricSystem() {
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
	private fun applyInitialLanguage() {
		if (prefs.exists(LANGUAGE_KEY)) {
			prefs.pull<Language>(LANGUAGE_KEY).also{ appLanguage = it }
		}
		//if not exists - apply ENGLISH language as app default
		else {
			with(ENGLISH) {
				prefs.push(LANGUAGE_KEY, this)
				appLanguage = this
			}
		}
	}
	
	//called only on app startup to pull saved value
	private fun applyInitialVinCode() {
		with(VEHICLE_VIN_CODE_KEY) {
			if (prefs.exists(this)) {
				prefs.pull<String>(this).also{ currentVehicleVinCode = it }
			}
		}
		
	}
}