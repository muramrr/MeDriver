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

package com.mmdev.me.driver.presentation.utils.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop


fun TextView.text(): String = if (!text.isNullOrBlank()) text.toString() else ""

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getStringRes(@StringRes stringRes: Int): String = resources.getString(stringRes)

fun Context.getTypeface(@FontRes fontRes: Int): Typeface? = ResourcesCompat.getFont(this, fontRes)

fun Context.getColorValue(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

fun Int.toIntArray(context: Context): IntArray {
	val typedArray = context.resources.obtainTypedArray(this)
	val drawableArray = IntArray(typedArray.length())
	for (i in drawableArray.indices) {
		drawableArray[i] = typedArray.getResourceId(i, 0)
	}
	//Recycles the TypedArray, to be re-used by a later caller.
	//After calling this function you must not ever touch the typed array again.
	typedArray.recycle()
	return drawableArray
}



inline fun <T: View> T.setDebounceOnClick(
	debounceTime: Long = 1000L, crossinline block: T.() -> Unit
) = setOnClickListener {
	if (tag != null && (tag as Long) > System.currentTimeMillis()) return@setOnClickListener
	else {
		tag = System.currentTimeMillis() + debounceTime
		block()
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

fun View.updateMargins(
	@Px start: Int = marginStart, @Px top: Int = marginTop, @Px end: Int = marginEnd,
	@Px bottom: Int = marginBottom
) {
	val params = layoutParams as MarginLayoutParams
	params.setMargins(start, top, end, bottom)
	this.layoutParams = params
}


fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()