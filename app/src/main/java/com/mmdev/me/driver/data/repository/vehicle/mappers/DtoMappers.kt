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

package com.mmdev.me.driver.data.repository.vehicle.mappers

import com.mmdev.me.driver.data.datasource.vehicle.local.entities.MaintenanceRegulationsEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.RegulationEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.server.dto.VehicleDto
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
		odometerValueBound = dto.odometerValue,
		engineCapacity = dto.engineCapacity,
		maintenanceRegulations = dto.maintenanceRegulations.mapKeys {
			PlannedParts.valueOf(it.key)
		},
		lastRefillDate = dto.lastRefillDate,
		lastUpdatedDate = dto.dateUpdated
	)
	
	/** Out: [VehicleEntity] */
	fun toEntity(dto: VehicleDto): VehicleEntity = VehicleEntity(
		brand = dto.brand,
		model = dto.model,
		year = dto.year,
		vin = dto.vin,
		odometerValueBound = dto.odometerValue,
		engineCapacity = dto.engineCapacity,
		maintenanceRegulations = maintenanceRegulationsMap(dto.maintenanceRegulations),
		lastRefillDate = dto.lastRefillDate,
		lastUpdatedDate = dto.dateUpdated
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