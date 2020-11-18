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
		maintenanceRegulations = maintenanceRegulationsMap(domain.maintenanceRegulations)
	)
	
	private fun maintenanceRegulationsMap(input: Map<SparePart, Regulation?>): MaintenanceRegulationsEntity =
		MaintenanceRegulationsEntity(
			airFilter = regulationsMap(input[FILTER_AIR]),
			engineFilterOil = regulationsMap(input[ENGINE_OIL_FILTER]),
			cabinFilter = regulationsMap(input[FILTER_AIR]),
			breaksFluid = regulationsMap(input[FILTER_AIR]),
			fuelFilter = regulationsMap(input[FILTER_AIR]),
			grmKit = regulationsMap(input[FILTER_AIR]),
			tires = regulationsMap(input[FILTER_AIR]),
		)
	
	private fun regulationsMap(input: Regulation?): RegulationEntity? =
		if (input != null) RegulationEntity(input.distance, input.time) else null
	
	/** Out: [VehicleDto] */
	fun toDto(domain: Vehicle): VehicleDto = VehicleDto(
		brand = domain.brand,
		model = domain.model,
		year = domain.year,
		vin = domain.vin,
		odometerValueBound = domain.odometerValueBound,
		engineCapacity = domain.engineCapacity,
		maintenanceRegulations = domain.maintenanceRegulations.mapKeys {
			it.key.getSparePartName()
		}
	)
	
}