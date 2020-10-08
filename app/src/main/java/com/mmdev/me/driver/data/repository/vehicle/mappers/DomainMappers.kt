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
 * In [Vehicle] -> Out: [VehicleEntity], [VehicleDto]
 */

object DomainMappers {
	
	/** Out: [VehicleEntity] */
	fun toEntity(domain: Vehicle): VehicleEntity = VehicleEntity(
		brand = domain.brand,
		model = domain.model,
		year = domain.year,
		vin = domain.vin,
		odometerValueBound = domain.odometerValueBound,
		engineCapacity = domain.engineCapacity
	)
	
	/** Out: [VehicleDto] */
	fun toDto(domain: Vehicle): VehicleDto = VehicleDto(
		brand = domain.brand,
		model = domain.model,
		year = domain.year,
		vin = domain.vin,
		odometerValueBound = domain.odometerValueBound,
		engineCapacity = domain.engineCapacity
	)
	
}