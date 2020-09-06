/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.09.2020 00:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.slider.Slider

/**
 * Contains [Slider] value binding methods
 */

object BindingSlider {
	
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