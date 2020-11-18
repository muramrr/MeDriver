/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.11.2020 15:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vehicle.mappers

import com.mmdev.me.driver.data.datasource.vehicle.local.entities.MaintenanceRegulationsEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.RegulationEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.remote.dto.VehicleDto
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.ENGINE_OIL_FILTER
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.FILTER_AIR
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.vehicle.data.Regulation
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
		engineCapacity = entity.engineCapacity,
		maintenanceRegulations = maintenanceRegulationsMap(entity.maintenanceRegulations).mapKeys { it.key.getSparePartName() }
	)
	
	/** Out: [Vehicle] */
	fun toDomain(entity: VehicleEntity): Vehicle = Vehicle(
		brand = entity.brand,
		model = entity.model,
		year = entity.year,
		vin = entity.vin,
		odometerValueBound = entity.odometerValueBound,
		engineCapacity = entity.engineCapacity,
		maintenanceRegulations = maintenanceRegulationsMap(entity.maintenanceRegulations)
	)
	
	private fun maintenanceRegulationsMap(input: MaintenanceRegulationsEntity): Map<SparePart, Regulation?> =
		mapOf(
			FILTER_AIR to regulationsMap(input.airFilter),
			ENGINE_OIL_FILTER to regulationsMap(input.engineFilterOil)
		)
	
	private fun regulationsMap(input: RegulationEntity?): Regulation? =
		if (input != null) Regulation(input.distance, input.time) else null
	
}