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

package com.mmdev.me.driver.presentation.ui.garage

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Seems that class provided by library has not locale-aware number formatter, so here is the
 * full copy of [LargeValueFormatter] but with [Locale.ENGLISH] applied by default
 * Also added additional extra suffix which will be displayed after 'k', 'm', 'b' etc
 * In my case it is just a currency symbol
 */

class MyLargeValueFormatter(extraSuffix: String = ""): ValueFormatter() {
	private var mSuffix = arrayOf(
		"", "k", "m", "b", "t"
	)
	private var mMaxLength = 5
	private val mFormat: DecimalFormat = DecimalFormat("###E00", DecimalFormatSymbols(Locale.ENGLISH))
	private val mExtraSuffix = " $extraSuffix"
	
	override fun getFormattedValue(value: Float): String = makePretty(value.toDouble())
	
	private fun makePretty(number: Double): String {
		var r = mFormat.format(number)
		val numericValue1 = Character.getNumericValue(r[r.length - 1])
		val numericValue2 = Character.getNumericValue(r[r.length - 2])
		val combined = "$numericValue2$numericValue1".toInt()
		r = r.replace("E[0-9][0-9]".toRegex(), mSuffix[combined / 3])
		while (r.length > mMaxLength || r.matches(Regex("[0-9]+\\.[a-z]"))) {
			r = r.substring(0, r.length - 2) + r.substring(r.length - 1)
		}
		return "$r$mExtraSuffix"
	}
	
}