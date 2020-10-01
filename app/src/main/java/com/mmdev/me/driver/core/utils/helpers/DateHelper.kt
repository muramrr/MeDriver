/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.10.2020 19:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils.helpers

import java.util.*

/**
 * Used to convert dates
 */

object DateHelper {
	
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
	
	
}