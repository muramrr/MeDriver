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

package com.mmdev.me.driver.presentation.utils.binding

import android.animation.ValueAnimator
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem.*
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.vehicle.data.Regulation
import com.mmdev.me.driver.presentation.utils.binding.BindingTextView.mapOfDoubleValues
import com.mmdev.me.driver.presentation.utils.extensions.domain.getOdometerFormatted
import com.mmdev.me.driver.presentation.utils.extensions.domain.getYearsFormatted
import com.mmdev.me.driver.presentation.utils.extensions.domain.humanDate
import com.mmdev.me.driver.presentation.utils.extensions.getStringRes
import kotlinx.datetime.LocalDate

/**
 * Contains binding methods related to [TextView]
 *
 * [mapOfIntValues] and [mapOfDoubleValues] contains Pair of [TextView] id and [Number] used
 * to smooth animate from last value instead of jumping to 0 each time function invoked
 */

object BindingTextView {
	
	@JvmStatic
	@BindingAdapter("android:text")
	fun setTextRes(textView: TextView, resource: Int) {
		if (resource == 0) return
		textView.apply { text = this.getStringRes(resource) }
	}
	
	@JvmStatic
	@BindingAdapter("android:textColor")
	fun setTextColor(textView: TextView, @ColorRes color: Int) {
		textView.setTextColor(ContextCompat.getColor(textView.context, color))
	}
	
	@JvmStatic
	@BindingAdapter("android:drawableStart")
	fun setDrawableStart(textView: TextView, @DrawableRes drawable: Int) {
		textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0)
	}
	
	@JvmStatic
	@BindingAdapter("app:kilometers", "app:miles", requireAll = true)
	fun setTextMetricSysDependent(textView: TextView, kilometers: String, miles: String) {
		when (MedriverApp.metricSystem) {
			KILOMETERS -> textView.text = kilometers
			MILES -> textView.text = miles
		}
	}
	
	@JvmStatic
	@BindingAdapter("app:suffixKM", "app:suffixMI", requireAll = true)
	fun setSuffixMetricSysDependent(inputLayout: TextInputLayout, kilometers: String, miles: String) {
		when (MedriverApp.metricSystem) {
			KILOMETERS -> inputLayout.suffixText = kilometers
			MILES -> inputLayout.suffixText = miles
		}
	}
	
	@JvmStatic
	@BindingAdapter("app:regulation")
	fun setRegulation(textView: TextView, regulation: Regulation?) {
		textView.apply {
			text = if (regulation != null) {
				getStringRes(R.string.fg_vehicle_card_replacements_subtitle_formatter).format(
					regulation.distance.getOdometerFormatted(context),
					//due to subtleties of language, here is a method to check last char for year count
					getYearsFormatted(DateHelper.getYearsCount(regulation.time), context)
				)
			}
			else getStringRes(R.string.fg_vehicle_card_replacements_subtitle_no_vehicle)
		}
	}
	
	@JvmStatic
	@BindingAdapter("app:distanceRemain")
	fun setDistanceRemaining(textView: TextView, distanceBound: DistanceBound?) {
		textView.apply {
			text = distanceBound?.getOdometerFormatted(context) ?:
			       getStringRes(R.string.fg_vehicle_card_replacements_value_not_replaced)
		}
	}
	
	@JvmStatic
	@BindingAdapter("app:dateUntil")
	fun setDistanceRemaining(textView: TextView, time: LocalDate?) {
		textView.apply {
			text = time?.humanDate() ?:
			       getStringRes(R.string.fg_vehicle_card_replacements_value_not_replaced)
		}
	}
	
	
	private val mapOfDoubleValues = HashMap<Int, Double>()
	@JvmStatic
	@BindingAdapter("app:animateDouble", "app:textTemplate", "app:defaultText", requireAll = true)
	fun setAnimatedValueDouble(textView: TextView, value: Double, template: String, defaultText: String) {
		val floatAnimator = ValueAnimator.ofFloat().apply {
			duration = 1200
			addUpdateListener {
				textView.text = template.format((it.animatedValue as Float).roundTo(2).toString())
			}
		}
		
		if (value > 0) {
			//init start to end values
			with(mapOfDoubleValues[textView.id] ?: 0.0 ) {
				if (this == value) floatAnimator.setFloatValues(0f, value.toFloat())
				else floatAnimator.setFloatValues(this.toFloat(), value.toFloat())
			}
			floatAnimator.start()
			mapOfDoubleValues[textView.id] = value
		}
		else {
			textView.text = defaultText
			mapOfDoubleValues[textView.id] = 0.0
		}
	}

}