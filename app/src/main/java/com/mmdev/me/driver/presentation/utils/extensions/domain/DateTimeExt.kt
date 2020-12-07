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

package com.mmdev.me.driver.presentation.utils.extensions.domain

import com.mmdev.me.driver.core.utils.helpers.DateHelper
import kotlinx.datetime.LocalDate
import java.util.*

/**
 * Extension functions to represent ui friendly date and time related objects
 * Used to free domain classes from application logic and use inside xml DataBinding
 */

fun Int.dateMonthText(): String = DateHelper.getMonthText(this, Locale.getDefault())

// date in format "01.01.1970"
fun LocalDate?.humanDate(default: String = ""): String = this?.let {
	(if (dayOfMonth < 10) "0$dayOfMonth." else "$dayOfMonth.") +
	(if (monthNumber < 10) "0$monthNumber." else "$monthNumber.") +
	"$year"
} ?: default