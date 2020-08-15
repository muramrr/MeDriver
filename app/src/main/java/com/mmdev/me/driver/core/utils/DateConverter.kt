/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.08.2020 20:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Used to convert dates
 */

object DateConverter {
	private val getMonthIntFormatter = SimpleDateFormat("MM", Locale.ENGLISH)
	
	private val getMonthTextFormatter = SimpleDateFormat("MMMM", Locale.getDefault())
	
	//used to make http request with such pattern as argument
	private val networkRequestFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
	
	//used primary to display in UI
	private val humanFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
	
	
	fun toText(date: Date): String = humanFormat.format(date)
	
	fun toDate(text: String): Date = humanFormat.parse(text)
	
	
	fun toFuelPriceRequestString(date: Date): String = networkRequestFormat.format(date)
	
	/**
	 * Make an Int Month from a date
	 */
	fun Date.getMonthInt(): Int = getMonthIntFormatter.format(this).toInt()
	
	/**
	 * Make a full month text
	 */
	fun Date.getMonthText() = getMonthTextFormatter.format(this)
	
}