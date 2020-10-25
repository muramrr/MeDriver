/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.10.2020 20:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.extensions.domain

import android.content.Context
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants

/**
 * [VehicleSparePart] extensions to use inside xml through data binding
 */

fun VehicleSparePart.getRelatedString(context: Context): String {
	return if (systemNodeComponent.getSparePartName() != SparePart.OTHER) {
		val pos = systemNode.getChildren().indexOf(systemNodeComponent)
		val childrenResArray = VehicleSystemNodeConstants.childrenMap[systemNode]!!
		context.getString(childrenResArray[pos])
	}
	else searchCriteria.first()
	
}
