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