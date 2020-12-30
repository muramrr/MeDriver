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