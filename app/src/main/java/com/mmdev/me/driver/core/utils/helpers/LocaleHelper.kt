/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.core.utils.helpers

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.Language.*
import java.util.*

/**
 * Designed to wrap locale changes in application or other locale specified manipulations
 * Also contains constants for supported languages
 */

object LocaleHelper {
	
	private const val LANGUAGE_UKRAINIAN = "uk"
	private const val COUNTRY_UKRAINE = "UA"
	
	private const val LANGUAGE_RUSSIAN = "ru"
	private const val COUNTRY_RUSSIA = "RU"
	
	val UKRAINIAN_LOCALE: Locale = Locale(LANGUAGE_UKRAINIAN, COUNTRY_UKRAINE)
	val RUSSIAN_LOCALE: Locale = Locale(LANGUAGE_RUSSIAN, COUNTRY_RUSSIA)
	val ENGLISH_LOCALE: Locale = Locale.ENGLISH
	
	val supportedLocales: Array<Locale> = arrayOf(UKRAINIAN_LOCALE, RUSSIAN_LOCALE, ENGLISH_LOCALE)
	
	fun newLocaleContext(context: Context, language: Language): Context {
		
		val savedLocale = when (language) {
			UKRAINIAN -> UKRAINIAN_LOCALE
			RUSSIAN -> RUSSIAN_LOCALE
			ENGLISH -> ENGLISH_LOCALE
		} // else return the original context
		
		
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
        } // else return the original context
		
		Locale.setDefault(savedLocale)
		
		// create new configuration with the saved locale
		val newConfig = Configuration()
		newConfig.setLocale(savedLocale)
		
		
		// as part of creating a new context that contains the new locale we also need to override the default locale.
		// override the locale on the given context (Activity, Fragment, etc...)
		// override the locale on the application context
		if (context != context.applicationContext) {
			context.applicationContext.resources.apply {
				updateConfiguration(newConfig, displayMetrics)
			}
		}
		else {
			context.resources.updateConfiguration(newConfig, context.resources.displayMetrics)
		}
	}
	
	fun getStringByLocale(
		context: Context,
		@StringRes stringRes: Int,
		locale: Locale,
		vararg formatArgs: Any
	): String {
		val configuration = Configuration(context.resources.configuration)
		configuration.setLocale(locale)
		return context.createConfigurationContext(configuration).resources.getString(stringRes, *formatArgs)
	}
	
	fun getStringFromAllLocales(
		context: Context,
		@StringRes stringRes: Int,
		vararg formatArgs: Any
	): Map<Locale, String> {
		val result: MutableMap<Locale, String> = mutableMapOf()
		
		val configuration = Configuration(context.resources.configuration)
		
		supportedLocales.forEach {
			configuration.setLocale(it)
			result[it] = context.createConfigurationContext(configuration).resources.getString(stringRes, *formatArgs)
		}
		
		return result
	}
}