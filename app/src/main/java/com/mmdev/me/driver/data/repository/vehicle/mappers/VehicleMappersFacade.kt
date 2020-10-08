/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 21:33
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
 * mapping between sources [dto -> dbEntity], [dbEntity -> DomainModel], [dto -> DomainModel]
 *
 * DTO = Data Transfer Object
 * DB = database
 * Entity = room database @Entity data class
 * DM = Domain Model
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
	fun apiDtoToDomain(dto: VehicleDto): Vehicle =
		DtoMappers.toDomain(dto)
	
	fun apiDtoToDbEntity(dto: VehicleDto): VehicleEntity =
		DtoMappers.toEntity(dto)
	
	fun listApiDtoToDomain(input: List<VehicleDto>): List<Vehicle> =
		mapList(input) { apiDtoToDomain(it) }
	
	fun listApiDtoToDbEntity(input: List<VehicleDto>): List<VehicleEntity> =
		mapList(input) { apiDtoToDbEntity(it) }
	
	
	
	
	// in: domain, out: * entity, dto
	fun domainToDbEntity(domain: Vehicle): VehicleEntity =
		DomainMappers.toEntity(domain)
	
	fun domainToApiDto(domain: Vehicle): VehicleDto =
		DomainMappers.toDto(domain)
	
	
	
	
	// in: entity, out: * domain, dto
	fun dbEntityToApiDto(entity: VehicleEntity): VehicleDto =
		EntityMappers.toDto(entity)
	
	fun dbEntityToDomain(entity: VehicleEntity): Vehicle =
		EntityMappers.toDomain(entity)
	
	fun listDbEntityToDomain(input: List<VehicleEntity>): List<Vehicle> =
		mapList(input) { dbEntityToDomain(it) }
	
	fun listDbEntityToDtos(input: List<VehicleEntity>): List<VehicleDto> =
		mapList(input) { dbEntityToApiDto(it) }
	
	
}
