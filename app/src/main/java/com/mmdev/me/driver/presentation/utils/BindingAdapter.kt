/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.08.2020 17:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.mmdev.me.driver.presentation.ui.common.BindableAdapter



object BindingAdapterUtils {
	
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
	
	/**
	 * apply Int Drawable res or color as resource in xml
	 * For example:
	 * android:src="@{viewModel.drawable}"
	 * val drawable = MutableLiveData<Int>(R.drawable.some_drawable)
	 */
	@JvmStatic
	@BindingAdapter("android:src")
	fun setImageResource(imageView: ImageView, resource: Int) {
		imageView.setImageResource(resource)
	}
	
	@JvmStatic
	@BindingAdapter("app:bindData")
	@Suppress("UNCHECKED_CAST")
	fun <T> setRecyclerViewAdapter(recyclerView: RecyclerView, newData: T) {
		if (recyclerView.adapter is BindableAdapter<*>) {
			(recyclerView.adapter as BindableAdapter<T>).setNewData(newData)
		}
	}
	
	/**
	 * Hides keyboard when the [EditText] is focused.
	 *
	 * There can only be one [TextView.OnEditorActionListener] on each [EditText] and
	 * this [BindingAdapter] sets it.
	 */
	@JvmStatic
	@BindingAdapter("app:hideKeyboardOnInputDone")
	fun hideKeyboardOnInputDone(inputView: EditText, enabled: Boolean) {
		if (!enabled) return
		val listener = TextView.OnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				inputView.hideKeyboard(inputView)
			}
			false
		}
		inputView.setOnEditorActionListener(listener)
	}
	
	//getter for slider value
	@JvmStatic
	@InverseBindingAdapter(attribute = "android:value")
	fun getSliderValue(slider: Slider): Float = slider.value
	
	//change listener for slider value
	@JvmStatic
	@BindingAdapter("android:valueAttrChanged")
	fun setSliderListeners(slider: Slider, attrChange: InverseBindingListener) {
		slider.addOnChangeListener { _, _, _ ->
			attrChange.onChange()
		}
	}
}