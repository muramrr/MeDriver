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

package com.mmdev.me.driver.presentation.ui.common

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes

/**
 * Generic adapter for Drop Down Lists used in AutoCompleteTextViews
 */

abstract class BaseDropAdapter<T>(
	context: Context,
	@LayoutRes private val layoutId: Int,
	private var data: List<T>
): ArrayAdapter<T>(context, layoutId, data) {
	
	override fun getItem(position: Int): T = data[position]
	
	override fun getCount(): Int = data.size
	
	open fun setNewData(data: List<T>) {
		this.data = data
		notifyDataSetChanged()
	}
	
}