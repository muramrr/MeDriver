/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.10.2020 19:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.maintenance.data.components

/**
 * Abstract class wrapper for [VehicleSystemNodeType]
 * Because of enums cannot be used as type parameter (or i don't know how to do that)
 *
 * Each of child is type safety to use inside [com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart]
 *
 * Each of this nodes can have different amount of child spare parts affiliated to [VehicleSystemNodeType]
 * @see com.mmdev.me.driver.domain.maintenance.data.components.parts.SparePart
 *
 * For example:
 * @see com.mmdev.me.driver.domain.maintenance.data.components.parts.EngineParts
 */

abstract class VehicleSystemNode(private val type: VehicleSystemNodeType) {
	
	fun getVehicleSystemNode(): VehicleSystemNodeType = type
	
	object ENGINE: VehicleSystemNode(VehicleSystemNodeType.ENGINE)
	object TRANSMISSION: VehicleSystemNode(VehicleSystemNodeType.TRANSMISSION)
	object ELECTRONICS: VehicleSystemNode(VehicleSystemNodeType.ELECTRICS)
	object SUSPENSION: VehicleSystemNode(VehicleSystemNodeType.SUSPENSION)
	object BRAKES: VehicleSystemNode(VehicleSystemNodeType.BRAKES)
	object BODY: VehicleSystemNode(VehicleSystemNodeType.BODY)
	object PLANNED: VehicleSystemNode(VehicleSystemNodeType.PLANNED)
	object OTHERS: VehicleSystemNode(VehicleSystemNodeType.OTHERS)
}