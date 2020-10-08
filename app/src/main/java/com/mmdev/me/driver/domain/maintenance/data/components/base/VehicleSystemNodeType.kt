/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 18:29
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
	
	ENGINE, TRANSMISSION, ELECTRICS, SUSPENSION, BRAKES, BODY, PLANNED, OTHERS;
	
	companion object {
		
		fun VehicleSystemNodeType.getChildBy(item: String): SparePart = when (this) {
			ENGINE -> EngineParts.valueOf(item)
			TRANSMISSION -> TransmissionParts.valueOf(item)
			ELECTRICS -> ElectricParts.valueOf(item)
			SUSPENSION -> SuspensionParts.valueOf(item)
			BRAKES -> BrakesParts.valueOf(item)
			BODY -> BodyParts.valueOf(item)
			PLANNED -> PlannedParts.valueOf(item)
			OTHERS -> OtherParts.valueOf(item)
		}
		
		fun VehicleSystemNodeType.getChildren(): Array<SparePart> = when (this) {
			ENGINE -> EngineParts.values() as Array<SparePart>
			TRANSMISSION -> TransmissionParts.values() as Array<SparePart>
			ELECTRICS -> ElectricParts.values() as Array<SparePart>
			SUSPENSION -> SuspensionParts.values() as Array<SparePart>
			BRAKES -> BrakesParts.values() as Array<SparePart>
			BODY -> BodyParts.values() as Array<SparePart>
			PLANNED -> PlannedParts.values() as Array<SparePart>
			OTHERS -> OtherParts.values() as Array<SparePart>
		}
	}
	
}