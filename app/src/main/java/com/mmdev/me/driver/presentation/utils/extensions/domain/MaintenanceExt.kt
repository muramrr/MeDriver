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

package com.mmdev.me.driver.presentation.utils.extensions.domain

import android.content.Context
import com.mmdev.me.driver.R
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants

/**
 * [VehicleSparePart] extensions to use inside xml through data binding
 */


/**
 * Checks if given [VehicleSparePart] single component doesn't have [OTHER] flag
 * If not -> get parent children components and find component
 * else return [searchCriteteria] first item because its always contains only one typed name
 */
fun VehicleSparePart.getRelatedString(context: Context): String {
	return if (systemNodeComponent.getSparePartName() != SparePart.OTHER) {
		val pos = systemNode.getChildren().indexOf(systemNodeComponent)
		val childRes = VehicleSystemNodeConstants.childrenMap[systemNode]!![pos]
		context.getString(childRes)
	}
	else searchCriteria.first() // other
}


fun VehicleSparePart.getVendorAndArticulus(context: Context): String {
	return if (vendor.isNotEmpty() && articulus.isNotEmpty()) "$vendor $articulus"
	else context.getString(R.string.undefined)
}