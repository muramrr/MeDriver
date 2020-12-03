/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 19:01
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
		lastRefillDate = entity.lastRefillDate
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
		lastRefillDate = entity.lastRefillDate
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