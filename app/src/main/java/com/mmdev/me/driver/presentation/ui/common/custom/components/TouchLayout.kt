/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.10.2020 17:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.custom.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Custom parent to intercept all touches events to prevent being clicked any of view that is
 * showing below this viewGroup
 */

class TouchLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
	
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		return true
	}
	
}
