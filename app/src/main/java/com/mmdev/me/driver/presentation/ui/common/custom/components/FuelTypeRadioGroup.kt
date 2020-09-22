/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 17:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.custom.components


import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.mmdev.me.driver.R
import kotlin.math.roundToInt


/**
 * Custom RadioGroup container to store FuelType buttons chooser
 * Main Features: expand all buttons equally according to screen size, including gaps between
 */

class FuelTypeRadioGroup @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {
	
	private var itemChildWidth = 0
	private var childHorizontalMargin = 0
	private var finalChildSize = 0
	
	private var colorPrimary = 0
	private var colorSecondary = 0
	
	init {
		colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary)
		colorSecondary = ContextCompat.getColor(context, R.color.colorSecondary)
	}
	
	
	override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
		if (child is Button) {
			child.setOnClickListener {
				setAllButtonsToUnselectedState()
				invokeOnClickListener(child)
				setSelectedButtonToSelectedState(child)
			}
		}
		super.addView(child, index, params)
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		initSpecs(MeasureSpec.getSize(widthMeasureSpec))
		
		//force layout to be match_parent in width and height calculated child rect
		measureChildren(finalChildSize, finalChildSize)
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), finalChildSize)
	}
	
	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		var leftPosCord = 0
		initSpecs(width)
		
		children.forEach { child ->
			child.layout(leftPosCord + childHorizontalMargin / 2,
			             0 + paddingTop,
			             leftPosCord + finalChildSize + childHorizontalMargin / 2,
			             finalChildSize
			)
			leftPosCord += finalChildSize + childHorizontalMargin
		}
	}
	
	private fun initSpecs(width: Int) {
		//item width with margins between children
		itemChildWidth = width / childCount
		//use only 5% of available space per item for its margins
		childHorizontalMargin = (itemChildWidth * 0.1).roundToInt()
		finalChildSize = itemChildWidth - childHorizontalMargin
	}
	
	private var listener: ((view: View, id: Int) -> Unit)? = null
	
	/**
	 * Register a callback to be invoked when the child button is clicked
	 */
	fun setOnClickListener(listener: (view: View, id: Int) -> Unit) {
		this.listener = listener
	}
	
	private fun invokeOnClickListener(selectedButton: View) {
		listener?.invoke(selectedButton, selectedButton.id)
	}
	
	/**
	 * Set child button selected by default if such exists
	 */
	fun setInitialSelected(id: Int) {
		children.find { it.id == id }?.let {
			if (it is Button) {
				setAllButtonsToUnselectedState()
				setSelectedButtonToSelectedState(it)
			}
		}
	}
	
	private fun setAllButtonsToUnselectedState() {
		children.forEach { child ->
			if (child is Button) setButtonToUnselectedState(child)
		}
	}
	
	private fun setButtonToUnselectedState(button: Button) {
		button.setBackgroundColor(Color.TRANSPARENT)
		button.setTextColor(colorPrimary)
	}
	
	private fun setSelectedButtonToSelectedState(selectedButton: Button) {
		selectedButton.setBackgroundColor(colorSecondary)
		selectedButton.setTextColor(Color.BLACK)
	}
	
}
