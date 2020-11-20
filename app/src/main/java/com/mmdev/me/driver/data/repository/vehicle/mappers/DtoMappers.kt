/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.11.2020 17:39
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
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.*
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
	
	private fun maintenanceRegulationsMap(input: Map<String, Regulation>): MaintenanceRegulationsEntity =
		MaintenanceRegulationsEntity(
			insurance = regulationsMap(input[INSURANCE.name] ?: error("No such element")),
			airFilter = regulationsMap(input[FILTER_AIR.name] ?: error("No such element")),
			engineFilterOil = regulationsMap(input[ENGINE_OIL_FILTER.name] ?: error("No such element")),
			fuelFilter = regulationsMap(input[FILTER_FUEL.name] ?: error("No such element")),
			cabinFilter = regulationsMap(input[FILTER_CABIN.name] ?: error("No such element")),
			brakesFluid = regulationsMap(input[BRAKES_FLUID.name] ?: error("No such element")),
			grmKit = regulationsMap(input[GRM_KIT.name] ?: error("No such element")),
			plugs = regulationsMap(input[PLUGS.name] ?: error("No such element")),
			transmissionFilterOil = regulationsMap(input[TRANSMISSION_OIL_FILTER.name] ?: error("No such element"))
		)
	
	private fun regulationsMap(input: Regulation): RegulationEntity =
		RegulationEntity(input.distance, input.time)
	
}