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
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter


object BindingCommon {
	
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
	@BindingAdapter("android:drawableEnd")
	fun setDrawableRightRes(textView: TextView, resource: Int) {
		if (resource == 0) return
		textView.apply {
			setCompoundDrawablesRelativeWithIntrinsicBounds(
				null,
				null,
				ContextCompat.getDrawable(this.context, resource),
				null
			)
		}
	}
	
	@JvmStatic
	@BindingAdapter("android:backgroundColor")
	fun setBackgroundColor(view: View, @ColorRes color: Int) {
		view.setBackgroundColor(ContextCompat.getColor(view.context, color))
	}
}