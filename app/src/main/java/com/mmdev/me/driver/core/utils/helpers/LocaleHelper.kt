/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.09.2020 17:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.helpers

import android.content.Context
import android.content.res.Configuration
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.Language.ENGLISH
import com.mmdev.me.driver.core.utils.Language.RUSSIAN
import com.mmdev.me.driver.core.utils.Language.UKRAINIAN
import java.util.*

/**
 * Designed to wrap locale changes in application
 */

object LocaleHelper {
	
	private const val LANGUAGE_UKRAINIAN = "uk"
	private const val COUNTRY_UKRAINE = "UA"
	
	private const val LANGUAGE_RUSSIAN = "ru"
	private const val COUNTRY_RUSSIA = "RU"
	
	val UKRAINIAN_LOCALE = Locale(LANGUAGE_UKRAINIAN, COUNTRY_UKRAINE)
	val RUSSIAN_LOCALE = Locale(LANGUAGE_RUSSIAN, COUNTRY_RUSSIA)
	val ENGLISH_LOCALE = Locale.ENGLISH
	
	fun newLocaleContext(context: Context, language: Language): Context {
		
		val savedLocale = when (language) {
			UKRAINIAN -> UKRAINIAN_LOCALE
			RUSSIAN -> RUSSIAN_LOCALE
			ENGLISH -> ENGLISH_LOCALE
		} ?: return context // else return the original context
		
		
		// as part of creating a new context that contains the new locale
		// we also need to override the default locale.
		Locale.setDefault(savedLocale)
		
		// create new configuration with the saved locale
		val newLocaleConfig = Configuration()
		newLocaleConfig.setLocale(savedLocale)
		
		//context.applyOverrideConfiguration(newLocaleConfig)
		
		return context.createConfigurationContext(newLocaleConfig)
	}
	
	fun overrideLocale(context: Context, language: Language) {
		
		val savedLocale = when (language) {
			UKRAINIAN -> UKRAINIAN_LOCALE
            RUSSIAN -> RUSSIAN_LOCALE
			ENGLISH -> ENGLISH_LOCALE
        } ?: return // else return the original context
		
		Locale.setDefault(savedLocale)
		
		// create new configuration with the saved locale
		val newConfig = Configuration()
		newConfig.setLocale(savedLocale)
		
		// as part of creating a new context that contains the new locale we also need to override the default locale.
		// override the locale on the given context (Activity, Fragment, etc...)
		context.resources.updateConfiguration(newConfig, context.resources.displayMetrics)
		
		// override the locale on the application context
		if (context != context.applicationContext) {
			context.applicationContext.resources.run { updateConfiguration(newConfig, displayMetrics) }
		}
	}
	
}