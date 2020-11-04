/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.11.2020 20:54
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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