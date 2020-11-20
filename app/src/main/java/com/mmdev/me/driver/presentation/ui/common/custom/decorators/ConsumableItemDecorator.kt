/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.11.2020 21:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.custom.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.presentation.utils.extensions.toPx

/**
 *
 */

class ConsumableVerticalItemDecorator(
	private val onlyBetweenPadding: Boolean = false
): RecyclerView.ItemDecoration() {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		
		val position = parent.getChildAdapterPosition(view)
		
		if (position == 0) outRect.bottom = 4.toPx()
		else {
			outRect.top = 4.toPx()
			outRect.bottom = 1.toPx()
		}
		
	}
	
}