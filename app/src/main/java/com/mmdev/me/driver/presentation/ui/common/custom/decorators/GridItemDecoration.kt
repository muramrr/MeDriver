/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.10.2020 20:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.custom.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


/**
 * Grid decoration to force same gaps between items
 */

class GridItemDecoration: ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

		val position = parent.getChildAdapterPosition(view)
		val spanIndex = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex

		

		if (spanIndex == 0) {
			outRect.left = 30
			outRect.right = 15
		}
		else { //if you just have 2 span . Or you can use (staggeredGridLayoutManager.getSpanCount()-1) as last span
			outRect.left = 15
			outRect.right = 30
		}
		outRect.bottom = 30
	}

}