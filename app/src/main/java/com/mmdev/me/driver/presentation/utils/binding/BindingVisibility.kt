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
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard

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