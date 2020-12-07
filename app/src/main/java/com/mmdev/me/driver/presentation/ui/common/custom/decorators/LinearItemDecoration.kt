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

/**
 * Linear recyclerView ItemDecoration used to make same gaps between all elements
 */

class LinearItemDecoration(
	private val orientation: Int = RecyclerView.VERTICAL,
	private val predefinedAvailableSpace: Int? = null,
	private val predefinedAvailableSpaceSecondary: Int? = null
): RecyclerView.ItemDecoration() {

	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

		val position = parent.getChildAdapterPosition(view)

		// Add top margin only for the first item to avoid double space between items
		when (orientation) {
			RecyclerView.VERTICAL -> {
				
				if (predefinedAvailableSpace != null) {
					val availableSpacePerItem = predefinedAvailableSpace / (parent.adapter!!.itemCount + 1)
					if (position == 0) outRect.top = availableSpacePerItem
					outRect.bottom = availableSpacePerItem
				}
				else {
					if (position == 0) outRect.top = 20
					outRect.bottom = 20
				}
				
				if (predefinedAvailableSpaceSecondary != null) {
					outRect.left = predefinedAvailableSpaceSecondary / 2
					outRect.right = predefinedAvailableSpaceSecondary / 2
				}
				else {
					outRect.left = 15
					outRect.right = 15
				}
			}
			RecyclerView.HORIZONTAL -> {
				
				if (predefinedAvailableSpace != null){
					val availableSpacePerItem = predefinedAvailableSpace / (parent.adapter!!.itemCount + 1)
					if (position == 0) outRect.left = availableSpacePerItem
					outRect.right = availableSpacePerItem
				}
				else {
					if (position == 0) outRect.left = 20
					outRect.right = 20
				}
				
			}
		}
		
	}

}