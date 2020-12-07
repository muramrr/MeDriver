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