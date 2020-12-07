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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.mmdev.me.driver.presentation.utils.extensions.toPx


/**
 * Grid decoration to force same gaps between items
 */

class GridItemDecoration(private val onlyBetweenPadding: Boolean = false): ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

		val position = parent.getChildAdapterPosition(view)
		val spanIndex = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex

		if (!onlyBetweenPadding) {
			if (spanIndex == 0) {
				outRect.left = 30
				outRect.right = 15
			}
			else { //if you just have 2 span . Or you can use (staggeredGridLayoutManager.getSpanCount()-1) as last span
				outRect.left = 15
				outRect.right = 30
			}
		}
		else {
			if (spanIndex == 0) outRect.right = 4.toPx()
			else outRect.left = 4.toPx()
		}
		
		outRect.bottom = 8.toPx()
	}

}