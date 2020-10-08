/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 21:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.extensions

import android.graphics.Insets
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateMarginsRelative


fun View.applySystemWindowInsetsPadding(
	applyLeft: Boolean = false,
	applyTop: Boolean = false,
	applyRight: Boolean = false,
	applyBottom: Boolean = false
) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
		doOnApplyNewWindowInsets { targetView, insets, padding, _ ->

			val left = if (applyLeft) insets.left else 0
			val top = if (applyTop) insets.top else 0
			val right = if (applyRight) insets.right else 0
			val bottom = if (applyBottom) insets.bottom else 0

			targetView.setPadding(padding.left + left,
			                      padding.top + top,
			                      padding.right + right,
			                      padding.bottom + bottom)
		}
	}
	else {
		doOnApplyOldWindowInsets { targetView, insets, padding, _ ->

			val left = if (applyLeft) insets.systemWindowInsetLeft else 0
			val top = if (applyTop) insets.systemWindowInsetTop else 0
			val right = if (applyRight) insets.systemWindowInsetRight else 0
			val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

			targetView.setPadding(padding.left + left,
			                      padding.top + top,
			                      padding.right + right,
			                      padding.bottom + bottom)
		}
	}

}

fun View.applySystemWindowInsetsMargins(
	applyLeft: Boolean = false,
	applyTop: Boolean = false,
	applyRight: Boolean = false,
	applyBottom: Boolean = false
) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
		doOnApplyNewWindowInsets { targetView, insets, _, margins ->

			val left = if (applyLeft) insets.left else 0
			val top = if (applyTop) insets.top else 0
			val right = if (applyRight) insets.right else 0
			val bottom = if (applyBottom) insets.bottom else 0

			val layoutParams = targetView.layoutParams as ViewGroup.MarginLayoutParams
			layoutParams.updateMarginsRelative(margins.left + left,
			                                   margins.top + top,
			                                   margins.right + right,
			                                   margins.bottom + bottom)
			targetView.layoutParams = layoutParams
		}
	}
	else {
		doOnApplyOldWindowInsets { targetView, insets, _, margins ->

			val left = if (applyLeft) insets.systemWindowInsetLeft else 0
			val top = if (applyTop) insets.systemWindowInsetTop else 0
			val right = if (applyRight) insets.systemWindowInsetRight else 0
			val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

			val layoutParams = targetView.layoutParams as ViewGroup.MarginLayoutParams
			layoutParams.updateMarginsRelative(margins.left + left,
			                                   margins.top + top,
			                                   margins.right + right,
			                                   margins.bottom + bottom)
			targetView.layoutParams = layoutParams
		}
	}

}


@RequiresApi(Build.VERSION_CODES.R)
private fun View.doOnApplyNewWindowInsets(f: (View, Insets, InitialPadding, InitialMargins) -> Unit) {
	// Create a snapshot of the view's padding state
	val initialPadding = recordInitialPaddingForView(this)
	val initialMargins = recordInitialMarginsForView(this)
	// Set an actual OnApplyWindowInsetsListener which proxies to the given
	// lambda, also passing in the original padding state

	setOnApplyWindowInsetsListener { v, windowInsets ->
		val systemBarsInsets = windowInsets.getInsets(WindowInsets.Type.systemGestures() or
				                                              WindowInsets.Type.displayCutout())
		f(v, systemBarsInsets, initialPadding, initialMargins)
		windowInsets
	}

	// request some insets
	requestApplyInsetsWhenAttached()
}

private fun View.doOnApplyOldWindowInsets(f: (View, WindowInsets, InitialPadding, InitialMargins) -> Unit) {
	// Create a snapshot of the view's padding state
	val initialPadding = recordInitialPaddingForView(this)
	val initialMargins = recordInitialMarginsForView(this)
	// Set an actual OnApplyWindowInsetsListener which proxies to the given
	// lambda, also passing in the original padding state
	setOnApplyWindowInsetsListener { v, insets ->
		f(v, insets, initialPadding, initialMargins)
		// Always return the insets, so that children can also use them
		insets
	}

	// request some insets
	requestApplyInsetsWhenAttached()
}

private fun recordInitialMarginsForView(view: View) =
	InitialMargins(view.marginLeft, view.marginTop,
	               view.marginRight, view.marginBottom)

private fun recordInitialPaddingForView(view: View) =
	InitialPadding(view.paddingLeft, view.paddingTop,
	               view.paddingRight, view.paddingBottom)




private fun View.requestApplyInsetsWhenAttached() {
	if (isAttachedToWindow) {
		// We're already attached, just request as normal
		requestApplyInsets()
	} else {
		// We're not attached to the hierarchy, add a listener to
		// request when we are
		addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
			override fun onViewAttachedToWindow(v: View) {
				v.removeOnAttachStateChangeListener(this)
				v.requestApplyInsets()
			}

			override fun onViewDetachedFromWindow(v: View) = Unit
		})
	}
}

private data class InitialPadding(val left: Int, val top: Int,
                                  val right: Int, val bottom: Int)

private data class InitialMargins(val left: Int, val top: Int,
                                  val right: Int, val bottom: Int)