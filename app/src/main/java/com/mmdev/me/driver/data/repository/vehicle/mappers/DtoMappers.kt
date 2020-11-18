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
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.vehicle.data.Regulation
import com.mmdev.me.driver.domain.vehicle.data.Vehicle

/**
 * In [VehicleDto] -> Out: [Vehicle], [VehicleEntity]
 */

object DtoMappers {
	
	/** Out: [Vehicle] */
	fun toDomain(dto: VehicleDto): Vehicle = Vehicle(
		brand = dto.brand,
		model = dto.model,
		year = dto.year,
		vin = dto.vin,
		odometerValueBound = dto.odometerValueBound,
		engineCapacity = dto.engineCapacity,
		maintenanceRegulations = dto.maintenanceRegulations.mapKeys { PlannedParts.valueOf(it.key) }
	)
	
	/** Out: [VehicleEntity] */
	fun toEntity(dto: VehicleDto): VehicleEntity = VehicleEntity(
		brand = dto.brand,
		model = dto.model,
		year = dto.year,
		vin = dto.vin,
		odometerValueBound = dto.odometerValueBound,
		engineCapacity = dto.engineCapacity,
		maintenanceRegulations = maintenanceRegulationsMap(dto.maintenanceRegulations)
	)
	
	private fun maintenanceRegulationsMap(input: Map<String, Regulation?>): MaintenanceRegulationsEntity =
		MaintenanceRegulationsEntity(
			airFilter = regulationsMap(input[PlannedParts.FILTER_AIR.name]),
			engineFilterOil = regulationsMap(input[PlannedParts.ENGINE_OIL_FILTER.name]),
			cabinFilter = regulationsMap(input[PlannedParts.FILTER_AIR.name]),
			breaksFluid = regulationsMap(input[PlannedParts.FILTER_AIR.name]),
			fuelFilter = regulationsMap(input[PlannedParts.FILTER_AIR.name]),
			grmKit = regulationsMap(input[PlannedParts.FILTER_AIR.name]),
			tires = regulationsMap(input[PlannedParts.FILTER_AIR.name]),
		)
	
	private fun regulationsMap(input: Regulation?): RegulationEntity? =
		if (input != null) RegulationEntity(input.distance, input.time) else null
	
}