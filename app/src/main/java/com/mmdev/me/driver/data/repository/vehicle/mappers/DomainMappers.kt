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

import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.MaintenanceRegulationsEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.RegulationEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.server.dto.VehicleDto
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.*
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.vehicle.data.Regulation
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
		engineCapacity = domain.engineCapacity,
		maintenanceRegulations = maintenanceRegulationsMap(domain.maintenanceRegulations),
		lastRefillDate = domain.lastRefillDate,
		lastUpdatedDate = domain.lastUpdatedDate
	)
	
	private fun maintenanceRegulationsMap(input: Map<SparePart, Regulation>): MaintenanceRegulationsEntity =
		MaintenanceRegulationsEntity(
			insurance = regulationsMap(input[INSURANCE]),
			airFilter = regulationsMap(input[FILTER_AIR]),
			engineFilterOil = regulationsMap(input[ENGINE_OIL_FILTER]),
			fuelFilter = regulationsMap(input[FILTER_FUEL]),
			cabinFilter = regulationsMap(input[FILTER_CABIN]),
			brakesFluid = regulationsMap(input[BRAKES_FLUID]),
			grmKit = regulationsMap(input[GRM_KIT]),
			plugs = regulationsMap(input[PLUGS]),
			transmissionFilterOil = regulationsMap(input[TRANSMISSION_OIL_FILTER])
		)
	
	private fun regulationsMap(input: Regulation?): RegulationEntity =
		RegulationEntity(input?.distance ?: DistanceBound(), input?.time ?: DateHelper.YEAR_DURATION)
	
	/** Out: [VehicleDto] */
	fun toDto(domain: Vehicle): VehicleDto = VehicleDto(
		brand = domain.brand,
		model = domain.model,
		year = domain.year,
		vin = domain.vin,
		odometerValue = domain.odometerValueBound,
		engineCapacity = domain.engineCapacity,
		maintenanceRegulations = domain.maintenanceRegulations.mapKeys {
			it.key.getSparePartName()
		},
		lastRefillDate = domain.lastRefillDate,
		dateUpdated = domain.lastUpdatedDate
	)
	
}