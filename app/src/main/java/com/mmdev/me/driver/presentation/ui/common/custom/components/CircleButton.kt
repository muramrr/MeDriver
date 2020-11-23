/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.11.2020 16:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.custom.components

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton

/**
 * Utility button class with circle appearance
 */

@Deprecated("Use MaterialButton with round shape style")
class CircleButton : MaterialButton {
	constructor(context: Context): super(context)
	constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
	constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle)
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)
	}
}