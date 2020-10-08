/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
fun LocalDate.humanDate(): String =
	"$dayOfMonth." + (if (monthNumber < 10) "0$monthNumber" else "$monthNumber") + ".$year"