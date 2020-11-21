/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.11.2020 18:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
	else context.getString(R.string.not_defined)
}