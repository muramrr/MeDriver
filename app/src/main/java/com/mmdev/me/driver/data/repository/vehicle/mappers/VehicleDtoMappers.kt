/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 01:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vehicle.mappers

import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.remote.dto.VehicleDto
import com.mmdev.me.driver.domain.vehicle.model.Vehicle

/**
 * In [VehicleDto] -> Out: [Vehicle], [VehicleEntity]
 */

object VehicleDtoMappers {
	
	/** Out: [Vehicle] */
	fun apiDtoToDomain(dto: VehicleDto): Vehicle = Vehicle(
		brand = dto.brand,
		model = dto.model,
		year = dto.year,
		vin = dto.vin,
		odometerValueBound = dto.odometerValueBound
	)
	
	/** Out: [VehicleEntity] */
	fun apitDtoToDbEntity(dto: VehicleDto): VehicleEntity = VehicleEntity(
		brand = dto.brand,
		model = dto.model,
		year = dto.year,
		vin = dto.vin,
		odometerValueBound = dto.odometerValueBound
	)
	
}