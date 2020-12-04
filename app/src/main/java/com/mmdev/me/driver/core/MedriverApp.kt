/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 21:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.cioccarellia.ksprefs.KsPrefs
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mmdev.me.driver.core.billing.AppBillingClient
import com.mmdev.me.driver.core.di.DataSourceLocalModule
import com.mmdev.me.driver.core.di.DataSourceRemoteModule
import com.mmdev.me.driver.core.di.DatabaseModule
import com.mmdev.me.driver.core.di.FirebaseModule
import com.mmdev.me.driver.core.di.MappersModule
import com.mmdev.me.driver.core.di.NetworkModule
import com.mmdev.me.driver.core.di.RepositoryModule
import com.mmdev.me.driver.core.di.SyncModule
import com.mmdev.me.driver.core.di.ViewModelsModule
import com.mmdev.me.driver.core.notifications.NotificationWorker
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.Language.ENGLISH
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.extensions.getAndroidId
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.core.utils.log.DebugConfig
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.domain.fuel.prices.data.Region
import com.mmdev.me.driver.domain.fuel.prices.data.Region.KYIV
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Contains dependency modules
 * From top to bottom representing DI tree
 * For example: [ViewModelsModule] depends on [RepositoryModule]
 * [RepositoryModule] depends on [DataSourceLocalModule] and [DataSourceRemoteModule]
 * and so on.
 */

class MedriverApp: Application() {
	
	companion object {
		private const val TAG = "mylogs_MEDRIVERAPP"
		private const val JOB_TAG = "notification_Worker"
		
		private const val PREFERENCES_NAME = "settings"
		
		private const val THEME_MODE_KEY = "theme_mode"
		private const val METRIC_SYSTEM_KEY = "metric_system"
		private const val LANGUAGE_KEY = "language"
		private const val PRICES_REGION_KEY = "prices_region"
		private const val LAST_OPERATION_KEY = "last_operation"
		private const val VEHICLE_VIN_CODE_KEY = "vehicle_vin"
		
		//todo delete
		private const val GENERATED_DATA_KEY = "generated"
		
		private lateinit var appContext: Context
		val prefs by lazy {
			KsPrefs(appContext, PREFERENCES_NAME) {
				//	encryptionType = EncryptionType.KeyStore("key")
			}
		}
		
		val androidId: String by lazy { getAndroidId(appContext) }
		
		// public because the initial UI is built from these values
		// and some of these values are used across application
		
		var themeMode: ThemeMode = LIGHT_MODE
			set(value) {
				field = value
				prefs.push(THEME_MODE_KEY, value)
				logDebug(TAG, "AppTheme changed to $value")
				ThemeHelper.applyTheme(value)
			}
		
		var metricSystem: MetricSystem = KILOMETERS
			set(value) {
				field = value
				prefs.push(METRIC_SYSTEM_KEY, value)
				logDebug(TAG, "Metric system changed to $value")
			}
		
		var appLanguage: Language = ENGLISH
			set(value) {
				field = value
				prefs.push(LANGUAGE_KEY, value)
				logDebug(TAG, "Language changed to $value")
			}
		
		var pricesRegion: Region = KYIV
			set(value) {
				if (field != value) {
					field = value
					prefs.push(PRICES_REGION_KEY, value)
					logDebug(TAG, "Region changed to $value")
				}
			}
		
		var currentVehicleVinCode: String = ""
			set (value) {
				if (field != value) {
					field = value
					prefs.push(VEHICLE_VIN_CODE_KEY, value)
					logDebug(TAG, "Current Vehicle VIN changed to $value")
				}
			}
		
		@Volatile
		var lastOperationSyncedId: Long = 0
			@Synchronized set (value) {
				if (value > field) {
					field = value
					prefs.push(LAST_OPERATION_KEY, value)
					logDebug(TAG, "Last operation synced: $value")
				}
			}
		
		//todo delete
		var dataGenerated: Boolean = false
			set(value) {
				field = value
				prefs.push(GENERATED_DATA_KEY, value)
				logDebug(TAG, "DATA GENERATED? -$value")
			}
		
		@Volatile
		var isNetworkAvailable: Boolean = false
		
		
		fun isInternetWorking(): Boolean = if (isNetworkAvailable) {
			runBlocking {
				withContext(MyDispatchers.io()) {
					try {
						//ping firestore api
						val url = URL("https://firestore.googleapis.com/")
						//val url = URL("https://google.com")
						val connection = url.openConnection() as HttpURLConnection
						connection.connectTimeout = 10000
						connection.connect()
						//logWtf(TAG, "${connection.responseCode}")
						connection.responseCode in arrayOf(200, 404)
					} catch (e: Throwable) {
						e.printStackTrace()
						false
					}
				}
			}
		}
		else false
		
		
		val debug: DebugConfig = DebugConfig.Default
		
		val appBillingClient: AppBillingClient by lazy {
			AppBillingClient(appContext)
		}
	}
	
	
	
	override fun onCreate() {
		
		appContext = applicationContext
		appBillingClient.querySkuDetails()
		
		FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!debug.isEnabled)
		
		
		startKoin {
			androidContext(this@MedriverApp)
			if (debug.isEnabled) androidLogger()
			loadKoinModules(
				// The lines on which modules are written represents
				// some kind of "layers" inside dependencies.
				listOf(
					ViewModelsModule,
					RepositoryModule, MappersModule, SyncModule,
					DataSourceRemoteModule, DataSourceLocalModule,
					NetworkModule, FirebaseModule, DatabaseModule
				)
			)
		}
		
		initSavedParams()
		
		super.onCreate()
		logInfo(TAG, "loaded theme mode - $themeMode")
		logInfo(TAG, "loaded metric system - ${metricSystem.name}")
		logInfo(TAG, "loaded language - ${appLanguage.name}")
		logInfo(TAG, "loaded vehicle vin - $currentVehicleVinCode")
		logInfo(TAG, "last operation id - $lastOperationSyncedId")
		
		initNotificationWorker()
	}
	
	private fun initSavedParams() {
		/** if not exists - apply [LIGHT_MODE] as default theme */
		themeMode = loadInitialPropertyOrPushDefault(THEME_MODE_KEY, LIGHT_MODE).also {
			ThemeHelper.applyTheme(it)
		}
		
		/** if not exists - apply [KILOMETERS] as default metric system and save */
		metricSystem = loadInitialPropertyOrPushDefault(key = METRIC_SYSTEM_KEY, default = KILOMETERS)
		
		/** if not exists - apply [ENGLISH] language as app default */
		appLanguage = loadInitialPropertyOrPushDefault(key = LANGUAGE_KEY, default = ENGLISH)
		
		/** if not exists - apply [KYIV] region as default */
		pricesRegion = loadInitialPropertyOrPushDefault(key = PRICES_REGION_KEY, default = KYIV)
		
		/** if not exists - apply empty string as default */
		lastOperationSyncedId = loadInitialPropertyOrPushDefault(key = LAST_OPERATION_KEY, default = 0)
		
		/** if not exists - apply empty string as default */
		currentVehicleVinCode = loadInitialPropertyOrPushDefault(key = VEHICLE_VIN_CODE_KEY, default = "")
		
		//todo delete
		dataGenerated = loadInitialPropertyOrPushDefault(key = GENERATED_DATA_KEY, default = false)
	}
	
	//called only on app startup to pull saved value or assign default & also write default to prefs
	private inline fun <reified T : Any> loadInitialPropertyOrPushDefault(key: String, default: T): T {
		return if (prefs.exists(key)) {
			prefs.pull(key)
		}
		else {
			prefs.push(key, default)
			default
		}
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
	
//	override fun getWorkManagerConfiguration(): androidx.work.Configuration =
//		Builder()
//			.setMinimumLoggingLevel(Log.VERBOSE)
//			.build()
	
	private fun initNotificationWorker() {
		logDebug(TAG, "Enqueueing notification worker...")
		val notificationWorkRequest =
			PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS).build()
		
		WorkManager.getInstance(applicationContext)
			.enqueueUniquePeriodicWork(
				JOB_TAG,
				ExistingPeriodicWorkPolicy.KEEP,
				notificationWorkRequest
			)
	}
	
}