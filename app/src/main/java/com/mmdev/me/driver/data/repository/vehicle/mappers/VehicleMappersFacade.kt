/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 16:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vehicle.mappers

import com.mmdev.me.driver.core.utils.extensions.toCurrentTimeAndDate
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.remote.dto.VehicleDto
import com.mmdev.me.driver.data.datasource.vin.remote.dto.VehicleByVin
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

/**
 * Mapping between Vehicle DTOs/entities/domain data classes
 */

class VehicleMappersFacade {
	
	fun vinApiDtoToDomain(dto: VehicleByVin): Vehicle =
		Vehicle(
			brand = dto.vehicleBrand,
			model = dto.vehicleModel,
			year = dto.vehicleProducedYear.toIntOrNull() ?: 0,
			vin = dto.vehicleVinCode,
			odometerValueBound = DistanceBound(),
			engineCapacity = dto.vehicleEngineCapacity.toDoubleOrNull() ?: 0.0
		)
	
	// in: dto, out: * entity, domain
	fun dtoToEntity(dto: VehicleDto): VehicleEntity = DtoMappers.toEntity(dto)
	
	fun listDtoToDomain(input: List<VehicleDto>): List<Vehicle> =
		mapList(input) { DtoMappers.toDomain(it) }
	
	fun listDtoToEntity(input: List<VehicleDto>): List<VehicleEntity> =
		mapList(input) { dtoToEntity(it) }
	
	
	
	
	// in: domain, out: * entity, dto
	fun domainToEntity(domain: Vehicle): VehicleEntity = DomainMappers.toEntity(domain)
	
	fun domainToDto(domain: Vehicle): VehicleDto = DomainMappers.toDto(domain)
	
	
	
	
	// in: entity, out: * domain, dto
	fun entityToDto(entity: VehicleEntity): VehicleDto = EntityMappers.toDto(entity)
	
	fun listEntitiesToDomain(input: List<VehicleEntity>): List<Vehicle> =
		mapList(input) { EntityMappers.toDomain(it) }
	
	fun listEntitiesToDto(input: List<VehicleEntity>): List<VehicleDto> =
		mapList(input) { EntityMappers.toDto(it) }
	
	
	fun sparePartToReplacementCalculated(input: Map<String, VehicleSparePartEntity?>, vehicle: Vehicle):
			Map<SparePart, PendingReplacement?> = input.mapKeys { PlannedParts.valueOf(it.key) }
		.mapValues { entry ->
			entry.value?.let {
				PendingReplacement(
					componentSpecs = "${it.vendor} ${it.articulus}",
					distanceRemain = DistanceBound(
						vehicle.maintenanceRegulations[entry.key]!!.distance.kilometers -
						(vehicle.odometerValueBound.kilometers - it.odometerValueBound.kilometers),
						vehicle.maintenanceRegulations[entry.key]!!.distance.miles -
						(vehicle.odometerValueBound.miles - it.odometerValueBound.miles)
					),
					finalDate = (Instant.fromEpochMilliseconds(it.date).plus(
						DateTimePeriod(
							years = DateHelper.getYearsCount(
								vehicle.maintenanceRegulations[entry.key]!!.time
							)
						),
						TimeZone.currentSystemDefault()
					)).toCurrentTimeAndDate().date
				)
			}
			
		}
		
	
}
