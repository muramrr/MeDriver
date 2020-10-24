/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.10.2020 19:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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