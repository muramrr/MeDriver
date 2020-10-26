/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.10.2020 15:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.maintenance.data.components.base

import com.mmdev.me.driver.domain.maintenance.data.components.BodyParts
import com.mmdev.me.driver.domain.maintenance.data.components.BrakesParts
import com.mmdev.me.driver.domain.maintenance.data.components.ElectricParts
import com.mmdev.me.driver.domain.maintenance.data.components.EngineParts
import com.mmdev.me.driver.domain.maintenance.data.components.OtherParts
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.maintenance.data.components.SuspensionParts
import com.mmdev.me.driver.domain.maintenance.data.components.TransmissionParts

/**
 * Enumerated vehicle system nodes
 */

enum class VehicleSystemNodeType {
	
	ENGINE, TRANSMISSION, ELECTRICS, SUSPENSION, BRAKES, BODY, OTHER, PLANNED;
	
	companion object {
		
		val valuesArray = values()
		
		fun VehicleSystemNodeType.getChildBy(item: String): SparePart = when (this) {
			ENGINE -> EngineParts.valueOf(item)
			TRANSMISSION -> TransmissionParts.valueOf(item)
			ELECTRICS -> ElectricParts.valueOf(item)
			SUSPENSION -> SuspensionParts.valueOf(item)
			BRAKES -> BrakesParts.valueOf(item)
			BODY -> BodyParts.valueOf(item)
			OTHER -> OtherParts.valueOf(item)
			PLANNED -> PlannedParts.valueOf(item)
		}
		
		fun VehicleSystemNodeType.getChildren(): Array<SparePart> = when (this) {
			ENGINE -> EngineParts.valuesArray
			TRANSMISSION -> TransmissionParts.valuesArray
			ELECTRICS -> ElectricParts.valuesArray
			SUSPENSION -> SuspensionParts.valuesArray
			BRAKES -> BrakesParts.valuesArray
			BODY -> BodyParts.valuesArray
			OTHER -> OtherParts.valuesArray
			PLANNED -> PlannedParts.valuesArray
		}
	}
	
}