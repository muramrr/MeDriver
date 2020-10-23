/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.10.2020 20:54
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance

import com.mmdev.me.driver.R
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType

/**
 *
 */

object VehicleSystemNodeConstants {
	
	val childrenMap: Map<VehicleSystemNodeType, Int> = mapOf(
		VehicleSystemNodeType.ENGINE to R.array.maintenance_engine_components,
		VehicleSystemNodeType.TRANSMISSION to R.array.maintenance_transmission_components,
		VehicleSystemNodeType.ELECTRICS to R.array.maintenance_electric_components,
		VehicleSystemNodeType.SUSPENSION to R.array.maintenance_suspension_components,
		VehicleSystemNodeType.BRAKES to R.array.maintenance_brakes_components,
		VehicleSystemNodeType.BODY to R.array.maintenance_body_components,
		VehicleSystemNodeType.OTHER to R.array.maintenance_other_components,
		VehicleSystemNodeType.PLANNED to R.array.maintenance_planned_components
	)
	
	
}