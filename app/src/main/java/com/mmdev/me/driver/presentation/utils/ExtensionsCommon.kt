/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.08.20 15:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils

import android.content.res.Resources
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController

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


inline fun <T: View> T.setDebounceClick(debounceTime: Long = 1000L,
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

inline fun <T: View> T.setSingleClick(crossinline block: T.() -> Unit) = setOnClickListener {
	block()
	it.isClickable = false
	setOnClickListener(null)
}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

//This extension will able to pop any fragment instances from back stack
fun NavController.popBackStackAllInstances(destination: Int, inclusive: Boolean): Boolean {
	var popped: Boolean
	while (true) {
		popped = popBackStack(destination, inclusive)
		if (!popped) {
			break
		}
	}
	return popped
}