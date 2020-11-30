/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.11.2020 19:43
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.helpers

import com.mmdev.me.driver.core.utils.helpers.DateHelper.YEAR_DURATION
import java.util.*

/**
 * Used to convert dates, also contains some properly localized months names
 *
 * @property YEAR_DURATION contains duration of year (approx 365.25 days)
 */

object DateHelper {
	
	//various time range durations in milliseconds
	const val YEAR_DURATION = 31557600000 // (365.24 days)
	const val MONTH_DURATION = YEAR_DURATION / 12 //(30.44 days)
	const val HOUR_DURATION = 3600000
	const val DAY_DURATION = 86400000
	const val WEEK_DURATION = 604800000
	
	private val ukrainianMonths: Array<String> = arrayOf(
		"", // this corresponds to 0 position
		"Січень",
		"Лютий",
		"Березень",
		"Квітень",
		"Травень",
		"Червень",
		"Липень",
		"Серпень",
		"Вересень",
		"Жовтень",
		"Листопад",
		"Грудень"
	)
	
	private val russianMonths: Array<String> = arrayOf(
		"", // this corresponds to 0 position
		"Январь",
		"Февраль",
		"Март",
		"Апрель",
		"Май",
		"Июнь",
		"Июль",
		"Август",
		"Сентябрь",
		"Октябрь",
		"Ноябрь",
		"Декабрь"
	)
	
	private val englishMonths: Array<String> = arrayOf(
		"", // this corresponds to 0 position
		"January",
		"February",
		"March",
		"April",
		"May",
		"June",
		"July",
		"August",
		"September",
		"October",
		"November",
		"December"
	)
	
	fun getYearsCount(millis: Long): Int = (millis / YEAR_DURATION).toInt()
	
	fun getMonthText(monthNumber: Int, locale: Locale): String =
		if (monthNumber in 1..12) {
			
			when (locale) {
				LocaleHelper.UKRAINIAN_LOCALE -> ukrainianMonths[monthNumber]
				LocaleHelper.RUSSIAN_LOCALE -> russianMonths[monthNumber]
				LocaleHelper.ENGLISH_LOCALE -> englishMonths[monthNumber]
				else -> { "Locale is not supported" }
			}
		}
	else throw IllegalArgumentException("Wrong month number. Should be in range from 1 to 12")
	
	fun isYearLeap(year: Int): Boolean {
		
		return if (year % 4 == 0) {
			if (year % 100 == 0) {
				// year is divisible by 400, hence the year is a leap year
				year % 400 == 0
			} else true
		} else false
	}
	
}