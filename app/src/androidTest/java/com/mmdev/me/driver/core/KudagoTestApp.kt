package com.mmdev.me.driver.core

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * This is the documentation block about the class
 */

class MedriverTestApp: Application() {
	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidLogger()
			androidContext(this@MedriverTestApp)
			modules(emptyList())
		}
	}

	internal fun injectModule(module: Module) {
		loadKoinModules(module)
	}
}