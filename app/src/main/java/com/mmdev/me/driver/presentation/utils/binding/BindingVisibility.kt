/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.09.2020 00:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.binding

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mmdev.me.driver.presentation.utils.hideKeyboard

/**
 * Contains methods to hide or show views depends on specific actions
 */

object BindingVisibility {
	
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
	
	@JvmStatic
	@BindingAdapter("app:visibilityInvisible")
	fun setVisibilityInvisible(view: View, show: Boolean = false) {
		view.visibility = if (show) View.VISIBLE else View.INVISIBLE
	}
	
	@JvmStatic
	@BindingAdapter("app:visibilityGone")
	fun setVisibilityGone(view: View, show: Boolean = false) {
		view.visibility = if (show) View.VISIBLE else View.GONE
	}
	
}