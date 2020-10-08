/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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