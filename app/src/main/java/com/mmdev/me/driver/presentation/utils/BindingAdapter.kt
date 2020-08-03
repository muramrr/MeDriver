/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 20:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.presentation.ui.common.BindableAdapter


/**
 *
 */

object BindingAdapterUtils {

	@JvmStatic
	@BindingAdapter("app:applySystemWindowInsetsPaddingLeft",
	                "app:applySystemWindowInsetsPaddingTop",
	                "app:applySystemWindowInsetsPaddingRight",
	                "app:applySystemWindowInsetsPaddingBottom",
	                requireAll = false)
	fun applySystemWindowInsetsPadding(view: View,
	                                   applyLeft: Boolean,
	                                   applyTop: Boolean,
	                                   applyRight: Boolean,
	                                   applyBottom: Boolean) =
		view.applySystemWindowInsetsPadding(applyLeft, applyTop, applyRight, applyBottom)

	@JvmStatic
	@BindingAdapter("app:applySystemWindowInsetsMarginLeft",
	                "app:applySystemWindowInsetsMarginTop",
	                "app:applySystemWindowInsetsMarginRight",
	                "app:applySystemWindowInsetsMarginBottom",
	                requireAll = false)
	fun applySystemWindowInsetsMargin(view: View,
	                                   applyLeft: Boolean,
	                                   applyTop: Boolean,
	                                   applyRight: Boolean,
	                                   applyBottom: Boolean) =
		view.applySystemWindowInsetsMargins(applyLeft, applyTop, applyRight, applyBottom)
	
	
	@JvmStatic
	@BindingAdapter("android:src")
	fun setImageResource(imageView: ImageView, resource: Int) {
		imageView.setImageResource(resource)
	}
	
	@JvmStatic
	@BindingAdapter("app:bindData")
	@Suppress("UNCHECKED_CAST")
	fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, newData: T) {
		if (recyclerView.adapter is BindableAdapter<*>) {
			(recyclerView.adapter as BindableAdapter<T>).setNewData(newData)
		}
	}

}