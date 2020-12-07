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

@Deprecated("Use MaterialButtonToggleGroup with child buttons style")
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
	
	private var selectedButtonId: Int = 0
	
	init {
		colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary)
		colorSecondary = ContextCompat.getColor(context, R.color.colorSecondary)
	}
	
	
	override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
		if (child is Button) {
			child.setOnClickListener {
				setAllButtonsToUnselectedState()
				listener?.invoke(child, child.id)
				selectedButtonId = child.id
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
	
	/**
	 * Set child button selected by default if such exists
	 */
	fun setInitialSelected(id: Int) {
		children.find { it.id == id }?.let {
			if (it is Button) {
				setAllButtonsToUnselectedState()
				listener?.invoke(it, it.id)
				setSelectedButtonToSelectedState(it)
			}
		}
	}
	
	fun getSelectedButtonId(): Int = selectedButtonId
	
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
		selectedButtonId = selectedButton.id
		selectedButton.setBackgroundColor(colorSecondary)
		selectedButton.setTextColor(Color.BLACK)
	}
	
}
