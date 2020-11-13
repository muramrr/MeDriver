/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.11.2020 18:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.custom.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * Center first and last item, means that decorator will imitate -1 and size+1 item existing
 * Better use with SnapHelper
 */

class CentralFirstLastItemDecoration(private val availableSpace: Int): RecyclerView.ItemDecoration() {
	
	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		val position = parent.getChildViewHolder(view).adapterPosition
		if (position == 0 || position == state.itemCount - 1) {
			val displayWidth = parent.context.resources.displayMetrics.widthPixels
			val padding = if (availableSpace > displayWidth / 2)
				(displayWidth / 2f - availableSpace / 2f).roundToInt()
			else availableSpace / 2
			if (position == 0) outRect.left = padding
			else outRect.right = padding
		}
	}
	
}