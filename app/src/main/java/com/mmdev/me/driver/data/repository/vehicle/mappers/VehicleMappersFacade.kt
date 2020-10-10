/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 14:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vehicle.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.remote.dto.VehicleDto
import com.mmdev.me.driver.data.datasource.vin.remote.dto.VehicleByVin
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.vehicle.data.Vehicle

/**
 * MappersFacade used inside [com.mmdev.me.driver.data.repository.vehicle.VehicleRepositoryImpl]
 * contains mappers between layers [data <-> domain]
 * mapping between sources [dto -> entity], [entity -> Domain], [dto -> Domain]
 *
 * DTO = Data Transfer Object (used only to store inside FirebaseFirestore)
 * ENTITY = Room database data class (annotation @Entity)
 * DM = Domain class
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
	fun apiDtoToEntity(dto: VehicleDto): VehicleEntity = DtoMappers.toEntity(dto)
	
	fun listApiDtoToDomain(input: List<VehicleDto>): List<Vehicle> =
		mapList(input) { DtoMappers.toDomain(it) }
	
	fun listApiDtoToEntity(input: List<VehicleDto>): List<VehicleEntity> =
		mapList(input) { apiDtoToEntity(it) }
	
	
	
	
	// in: domain, out: * entity, dto
	fun domainToEntity(domain: Vehicle): VehicleEntity = DomainMappers.toEntity(domain)
	
	fun domainToApiDto(domain: Vehicle): VehicleDto = DomainMappers.toDto(domain)
	
	
	
	
	// in: entity, out: * domain, dto
	fun listEntitiesToDomain(input: List<VehicleEntity>): List<Vehicle> =
		mapList(input) { EntityMappers.toDomain(it) }
	
	fun listEntitiesToDto(input: List<VehicleEntity>): List<VehicleDto> =
		mapList(input) { EntityMappers.toDto(it) }
	
	
}
