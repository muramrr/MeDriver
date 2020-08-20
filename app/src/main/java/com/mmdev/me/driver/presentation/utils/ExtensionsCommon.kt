/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.08.2020 16:22
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils

import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * This is the documentation block about the class
 */

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
	observe(lifecycleOwner, object : Observer<T> {
		override fun onChanged(t: T?) {
			observer.onChanged(t)
			removeObserver(this)
		}
	})
}

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getStringRes(@StringRes stringRes: Int): String = resources.getString(stringRes)


inline fun <T: View> T.setDebounceOnClick(debounceTime: Long = 1000L,
                                          crossinline block: T.() -> Unit) =
	setOnClickListener {
		when {
			tag != null && (tag as Long) > System.currentTimeMillis() -> return@setOnClickListener
			else -> {
				tag = System.currentTimeMillis() + debounceTime
				block()
			}
		}
	}

inline fun <T: View> T.setSingleOnClick(crossinline block: T.() -> Unit) = setOnClickListener {
	block()
	it.isClickable = false
	setOnClickListener(null)
}

inline fun <T: TextView> T.setOnClickWithSelection(crossinline block: T.() -> Unit) = setOnClickListener {
	block()
	it.isSelected = true
}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()