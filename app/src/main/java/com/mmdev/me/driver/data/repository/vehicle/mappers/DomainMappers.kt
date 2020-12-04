/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 19:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
			insurance = regulationsMap(input[INSURANCE] ?: error("No such element")),
			airFilter = regulationsMap(input[FILTER_AIR] ?: error("No such element")),
			engineFilterOil = regulationsMap(input[ENGINE_OIL_FILTER] ?: error("No such element")),
			fuelFilter = regulationsMap(input[FILTER_FUEL] ?: error("No such element")),
			cabinFilter = regulationsMap(input[FILTER_CABIN] ?: error("No such element")),
			brakesFluid = regulationsMap(input[BRAKES_FLUID] ?: error("No such element")),
			grmKit = regulationsMap(input[GRM_KIT] ?: error("No such element")),
			plugs = regulationsMap(input[PLUGS] ?: error("No such element")),
			transmissionFilterOil = regulationsMap(input[TRANSMISSION_OIL_FILTER] ?: error("No such element"))
		)
	
	private fun regulationsMap(input: Regulation): RegulationEntity =
		RegulationEntity(input.distance, input.time)
	
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