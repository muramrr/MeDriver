/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.12.2020 14:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import java.util.*


fun View.showToast(text: String, length: Int = Toast.LENGTH_SHORT) =
	Toast.makeText(this.context, text, length).show()

/**
 * Show a SnackBar with [messageRes] resource
 */
fun View.showSnack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_SHORT) =
	Snackbar.make(this, messageRes, length).setAnchorView(this).show()

/**
 * Show a SnackBar with [message] string
 */
fun View.showSnack(message: String, length: Int = Snackbar.LENGTH_SHORT) =
	Snackbar.make(this, message, length).setAnchorView(this).show()


/**
 * Show a SnackBar with [messageRes] resource, execute [f] and show it
 * buttonSubmit.snack(R.string.name_submitted, SnackBar.LENGTH_LONG, { action() })
 */
inline fun View.showSnackWithAction(
	@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_SHORT, f: Snackbar.() -> Unit
) {
	val snack = Snackbar.make(this, messageRes, length)
	snack.f()
	snack.show()
}

/**
 * Show a SnackBar with [message] string, execute [f] and show it
 * buttonSubmit.snack(R.string.name_submitted, SnackBar.LENGTH_LONG, { action() })
 */
inline fun View.showSnackWithAction(
	message: String, length: Int = Snackbar.LENGTH_SHORT, f: Snackbar.() -> Unit
) {
	val snack = Snackbar.make(this, message, length)
	snack.f()
	snack.show()
}

/**
 * Show the view (visibility = View.VISIBLE)
 */
fun View.visible(delay: Int = 300) : View {
	if (visibility != View.VISIBLE) {
		//check if previous state was GONE to prevent unexpected crash "No such view"
		if (visibility == View.GONE) visibility = View.INVISIBLE
		clearAnimation()
		alpha = 0.0f
		visibility = View.VISIBLE
		animate().alpha(1.0f).setDuration(delay.toLong()).setListener(null)
	}
	return this
}

/**
 * Hide the view (visibility = [View.INVISIBLE])
 */
fun View.invisible(delay: Int = 300): View {
	if (visibility != View.INVISIBLE) {
		clearAnimation()
		animate().alpha(0.0f).setDuration(delay.toLong())
			.setListener(object: AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					visibility = View.INVISIBLE
				}
			})
		
	}
	return this
}

/**
 * Remove the view (visibility = View.GONE)
 */
fun View.gone(delay: Int = 300) : View {
	if (visibility != View.GONE) {
		clearAnimation()
		animate().alpha(0.0f).setDuration(delay.toLong())
			.setListener(object: AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					clearAnimation()
					visibility = View.GONE
				}
			})
	}
	return this
}


/**
 * Set visibility to [View.VISIBLE] if [condition] returns true else apply visibility from [otherwise]
 * @param otherwise if not specified explicitly -> use default visibility from given [View]
 */
inline fun View.visibleIf(otherwise: Int, delay: Int = 300, condition: () -> Boolean) : View {
	if (otherwise == View.INVISIBLE) {
		if (condition()) visible(delay) else invisible(delay)
	}
	if (otherwise == View.GONE) {
		if (condition()) visible(delay) else gone(delay)
	}
	return this
}

/**
 * Hide the view if [condition] returns true else apply visibility from [otherwise]
 * @param otherwise if not specified explicitly -> use default visibility from given [View]
 */
inline fun View.invisibleIf(delay: Int = 300, condition: () -> Boolean): View {
	if (condition()) invisible(delay) else visible(delay)
	return this
}

/**
 * Remove [View] if [condition] returns true else apply visibility from [otherwise]
 * @param otherwise if not specified explicitly -> use default visibility from given [View]
 */
inline fun View.goneIf(delay: Int = 300, condition: () -> Boolean): View {
	if (condition()) gone(delay) else visible(delay)
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
		return inputMethodManager.hideSoftInputFromWindow(
			applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS
		)
	} catch (ignored: RuntimeException) { }
	finally { inputViewFocused?.clearFocus() }
	return false
}


inline fun Button.setupDatePicker(
	minDate: Long = 0, maxDate: Long? = null, crossinline block: Calendar.() -> Unit
) {
	val calendar = Calendar.getInstance(TimeZone.getDefault())
	val currentYear = calendar.get(Calendar.YEAR)
	val currentMonth = calendar.get(Calendar.MONTH)
	val currentMonthDisplay = currentMonth + 1 // january corresponds to 0
	val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
	
	text = dateToText(currentDay, currentMonthDisplay, currentYear)
	
	val datePickerDialog = DatePickerDialog(this.context, { _, pickedYear, pickedMonth, pickedDay ->
		
		val pickedMonthDisplay = pickedMonth + 1 // january corresponds to 0
		
		// Display Selected date in Button
		text = dateToText(pickedDay, pickedMonthDisplay, pickedYear)
		
		calendar.set(pickedYear, pickedMonth, pickedDay)
		
		//inside block apply selected date to whatever you want
		calendar.block()
		
		//mViewModel.pickedDate = convertToLocalDateTime(pickedDate.timeInMillis)
		
		
	}, currentYear, currentMonth, currentDay)
	
	// if min not specified then minDate = start of epoch (0)
	datePickerDialog.datePicker.minDate = minDate
	
	// if max not specified then maxDate = current date
	datePickerDialog.datePicker.maxDate = maxDate ?: calendar.timeInMillis
	
	
	setOnClickListener { datePickerDialog.show() }
	
}

fun dateToText(day: Int, month: Int, year: Int): String =
	(if (day < 10) "0$day." else "$day.") + (if (month < 10) "0$month" else "$month") + ".$year"


/** @param formatter used to display snack in formatted way */
fun TextView.attachClickToCopyText(context: Context, @StringRes formatter: Int) {
	setOnClickListener {
		val textToCopy = text.toString()
		val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		val clip = ClipData.newPlainText(textToCopy, text)
		clipboard.setPrimaryClip(clip)
		showSnack(getStringRes(formatter).format(textToCopy))
	}
}