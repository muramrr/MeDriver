/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 00:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.presentation.ui.common.BindableAdapter



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
	@BindingAdapter("app:bindData")
	@Suppress("UNCHECKED_CAST")
	fun <T> setRecyclerViewAdapter(recyclerView: RecyclerView, newData: T) {
		if (recyclerView.adapter is BindableAdapter<*>) {
			(recyclerView.adapter as BindableAdapter<T>).setNewData(newData)
		}
	}
	
}