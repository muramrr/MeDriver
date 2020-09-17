/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.09.2020 17:36
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
 * Show a SnackBar with [messageRes] resource
 */
fun View.showSnack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_SHORT) =
	snack(messageRes, length) {}

/**
 * Show a SnackBar with [message] string
 */
fun View.showSnack(message: String, length: Int = Snackbar.LENGTH_SHORT) =
	snack(message, length) {}


/**
 * Show a SnackBar with [messageRes] resource, execute [f] and show it
 * buttonSubmit.snack(R.string.name_submitted, SnackBar.LENGTH_LONG, { action() })
 */
inline fun View.snack(@StringRes messageRes: Int,
                      length: Int = Snackbar.LENGTH_SHORT,
                      f: Snackbar.() -> Unit) {
	val snack = Snackbar.make(this, messageRes, length)
	snack.f()
	snack.show()
}

/**
 * Show a SnackBar with [message] string, execute [f] and show it
 * buttonSubmit.snack(R.string.name_submitted, SnackBar.LENGTH_LONG, { action() })
 */
inline fun View.snack(message: String,
                      length: Int = Snackbar.LENGTH_SHORT,
                      f: Snackbar.() -> Unit) {
	val snack = Snackbar.make(this, message, length)
	snack.f()
	snack.show()
}

/**
 * Show the view (visibility = View.VISIBLE)
 */
fun View.visible() : View {
	if (visibility != View.VISIBLE) {
		visibility = View.VISIBLE
	}
	return this
}


/**
 * Set visibility to [View.VISIBLE] if [condition] returns true else apply visibility from [otherwise]
 * @param otherwise if not specified explicitly -> use default visibility from given [View]
 */
inline fun View.visibleIf(otherwise: Int = this.visibility, condition: () -> Boolean) : View {
	if (otherwise in arrayOf(View.VISIBLE, View.INVISIBLE, View.GONE) )
		visibility = if (condition()) View.VISIBLE else otherwise
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
 * Remove [View] if [condition] returns true else apply visibility from [otherwise]
 * @param otherwise if not specified explicitly -> use default visibility from given [View]
 */
inline fun View.goneIf(otherwise: Int = this.visibility, condition: () -> Boolean) : View {
	if (otherwise in arrayOf(View.VISIBLE, View.INVISIBLE, View.GONE) )
		visibility = if (condition()) View.GONE else otherwise
	return this
}

/**
 * Hide the view (visibility = [View.INVISIBLE])
 */
fun View.invisible() : View {
	if (visibility != View.INVISIBLE) {
		visibility = View.INVISIBLE
	}
	return this
}

/**
 * Hide the view if [condition] returns true else apply visibility from [otherwise]
 * @param otherwise if not specified explicitly -> use default visibility from given [View]
 */
inline fun View.invisibleIf(otherwise: Int = this.visibility, condition: () -> Boolean) : View {
	if (otherwise in arrayOf(View.VISIBLE, View.INVISIBLE, View.GONE) )
		visibility = if (condition()) View.INVISIBLE else otherwise
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
fun View.hideKeyboard(inputViewFocused: View? = null): Boolean {
	try {
		val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		return inputMethodManager.hideSoftInputFromWindow(applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
	} catch (ignored: RuntimeException) { }
	finally { inputViewFocused?.clearFocus() }
	return false
}
