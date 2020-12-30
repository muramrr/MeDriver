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

package com.mmdev.me.driver.presentation.ui.maintenance.add.parent

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Data class that represents UI behavior of parent system node
 */

data class ParentNodeUi (
	@StringRes val title: Int,
	@StringRes val components: Int,
	val children: IntArray,
	@DrawableRes val icon: Int
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as ParentNodeUi
		
		if (title != other.title) return false
		if (components != other.components) return false
		if (!children.contentEquals(other.children)) return false
		if (icon != other.icon) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = title
		result = 31 * result + components
		result = 31 * result + children.contentHashCode()
		result = 31 * result + icon
		return result
	}
}