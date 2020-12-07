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

import android.view.View
import androidx.databinding.BindingAdapter
import com.mmdev.me.driver.presentation.utils.extensions.applySystemWindowInsetsMargins
import com.mmdev.me.driver.presentation.utils.extensions.applySystemWindowInsetsPadding

/**
 * Contains BindingAdapters which relates to windowInsets
 */

object BindingInsets {
	
	/**
	 * Apply padding to corresponded view
	 * Specify each parameter individually
	 */
	@JvmStatic
	@BindingAdapter(
		"app:applySystemWindowInsetsPaddingLeft",
		"app:applySystemWindowInsetsPaddingTop",
		"app:applySystemWindowInsetsPaddingRight",
		"app:applySystemWindowInsetsPaddingBottom",
		requireAll = false
	)
	fun applySystemWindowInsetsPadding(
		view: View,
		applyLeft: Boolean,
		applyTop: Boolean,
		applyRight: Boolean,
		applyBottom: Boolean
	) = view.applySystemWindowInsetsPadding(applyLeft, applyTop, applyRight, applyBottom)
	
	/**
	 * Apply margins to corresponded view
	 * Specify each parameter individually
	 */
	@JvmStatic
	@BindingAdapter(
		"app:applySystemWindowInsetsMarginLeft",
		"app:applySystemWindowInsetsMarginTop",
		"app:applySystemWindowInsetsMarginRight",
		"app:applySystemWindowInsetsMarginBottom",
		requireAll = false
	)
	fun applySystemWindowInsetsMargin(
		view: View,
		applyLeft: Boolean,
		applyTop: Boolean,
		applyRight: Boolean,
		applyBottom: Boolean
	) = view.applySystemWindowInsetsMargins(applyLeft, applyTop, applyRight, applyBottom)
	
}