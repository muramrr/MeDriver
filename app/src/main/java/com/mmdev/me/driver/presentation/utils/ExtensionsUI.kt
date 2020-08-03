/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.08.20 16:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.showToast(text: String, length: Int = Toast.LENGTH_SHORT) =
	Toast.makeText(this.context, text, length).show()

/**
 * Show a snackbar with [messageRes]
 */
fun View.showSnack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_SHORT) =
	snack(messageRes, length) {}

/**
 * Show a snackbar with [messageRes], execute [f] and show it
 * buttonSubmit.snack(R.string.name_submitted, Snackbar.LENGTH_LONG, { action() })
 */
inline fun View.snack(@StringRes messageRes: Int,
                      length: Int = Snackbar.LENGTH_SHORT,
                      f: Snackbar.() -> Unit) {
	val snack = Snackbar.make(this, messageRes, length)
	snack.f()
	snack.show()
}

/**
 * Show the view  (visibility = View.VISIBLE)
 */
fun View.visible() : View {
	if (visibility != View.VISIBLE) {
		visibility = View.VISIBLE
	}
	return this
}

/**
 * Show the view if [condition] returns true
 * (visibility = View.VISIBLE)
 */
inline fun View.visibleIf(condition: () -> Boolean) : View {
	if (visibility != View.VISIBLE && condition()) {
		visibility = View.VISIBLE
	}
	return this
}

/**
 * Remove the view (visibility = View.GONE)
 */
fun View.gone() : View {
	if (visibility != View.GONE) {
		visibility = View.GONE
	}
	return this
}

/**
 * Show the view if [condition] returns true
 * (visibility = View.VISIBLE)
 */
inline fun View.goneIf(condition: () -> Boolean) : View {
	if (visibility != View.GONE && condition()) {
		visibility = View.GONE
	}
	return this
}

/**
 * Extension method to show a keyboard for View.
 */
fun View.showKeyboard() {
	val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	this.requestFocus()
	imm.showSoftInput(this, 0)
}
/**
 * Try to hide the keyboard and returns whether it worked
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
 */
fun View.hideKeyboard(): Boolean {
	try {
		val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
	} catch (ignored: RuntimeException) { }
	return false
}
