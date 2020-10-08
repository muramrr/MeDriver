/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 21:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vehicle.mappers

import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.remote.dto.VehicleDto
import com.mmdev.me.driver.domain.vehicle.data.Vehicle

/**
 * In [VehicleEntity] -> Out: [Vehicle], [VehicleDto]
 */

object EntityMappers {
	
	
	/** Out: [VehicleDto] */
	fun toDto(entity: VehicleEntity): VehicleDto = VehicleDto(
		brand = entity.brand,
		model = entity.model,
		year = entity.year,
		vin = entity.vin,
		odometerValueBound = entity.odometerValueBound,
		engineCapacity = entity.engineCapacity
	)
	
	/** Out: [Vehicle] */
	fun toDomain(entity: VehicleEntity): Vehicle = Vehicle(
		brand = entity.brand,
		model = entity.model,
		year = entity.year,
		vin = entity.vin,
		odometerValueBound = entity.odometerValueBound,
		engineCapacity = entity.engineCapacity
	)
	
}