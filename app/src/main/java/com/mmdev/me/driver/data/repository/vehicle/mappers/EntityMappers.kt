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
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.*
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
		odometerValue = entity.odometerValueBound,
		engineCapacity = entity.engineCapacity,
		maintenanceRegulations = maintenanceRegulationsMap(entity.maintenanceRegulations).mapKeys {
			it.key.getSparePartName()
		},
		lastRefillDate = entity.lastRefillDate,
		dateUpdated = entity.lastUpdatedDate
	)
	
	/** Out: [Vehicle] */
	fun toDomain(entity: VehicleEntity): Vehicle = Vehicle(
		brand = entity.brand,
		model = entity.model,
		year = entity.year,
		vin = entity.vin,
		odometerValueBound = entity.odometerValueBound,
		engineCapacity = entity.engineCapacity,
		maintenanceRegulations = maintenanceRegulationsMap(entity.maintenanceRegulations),
		lastRefillDate = entity.lastRefillDate,
		lastUpdatedDate = entity.lastUpdatedDate
	)
	
	private fun maintenanceRegulationsMap(input: MaintenanceRegulationsEntity): Map<SparePart, Regulation> =
		mapOf(
			INSURANCE to regulationsMap(input.insurance),
			FILTER_AIR to regulationsMap(input.airFilter),
			ENGINE_OIL_FILTER to regulationsMap(input.engineFilterOil),
			FILTER_FUEL to regulationsMap(input.fuelFilter),
			FILTER_CABIN to regulationsMap(input.cabinFilter),
			BRAKES_FLUID to regulationsMap(input.brakesFluid),
			GRM_KIT to regulationsMap(input.grmKit),
			PLUGS to regulationsMap(input.plugs),
			TRANSMISSION_OIL_FILTER to regulationsMap(input.transmissionFilterOil),
		)
	
	private fun regulationsMap(input: RegulationEntity): Regulation = Regulation(input.distance, input.time)
	
}