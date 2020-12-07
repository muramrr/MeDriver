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

package com.mmdev.me.driver.data.repository.maintenance.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.maintenance.server.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart

/**
 * Mapping between VehicleSparePart DTOs/entities/domain data classes
 */

class MaintenanceMappersFacade {
	
	// in: entities, out: * dto, domain
	fun listEntitiesToDomains(input: List<VehicleSparePartEntity>): List<VehicleSparePart> =
		mapList(input) { EntityMappers.toDomain(it) }
	
	fun listEntitiesToDto(input: List<VehicleSparePartEntity>): List<VehicleSparePartDto> =
		mapList(input) { EntityMappers.toDto(it) }
	
	fun entityToDomain(entity: VehicleSparePartEntity): VehicleSparePart =
		EntityMappers.toDomain(entity)
	
	
	// in: domain, out: * dto, entity
	fun listDomainsToEntities(input: List<VehicleSparePart>): List<VehicleSparePartEntity> =
		mapList(input) { DomainMappers.toEntity(it) }
	
	fun listDomainsToDto(input: List<VehicleSparePart>): List<VehicleSparePartDto> =
		mapList(input) { DomainMappers.toDto(it) }
	
	
	// in: dto, out: * entities, domain
	fun dtoToEntity(dto: VehicleSparePartDto): VehicleSparePartEntity = DtoMappers.toEntity(dto)
	fun listDtoToDomains(input: List<VehicleSparePartDto>): List<VehicleSparePart> =
		mapList(input) { DtoMappers.toDomain(it) }
	
	fun listDtoToEntities(input: List<VehicleSparePartDto>): List<VehicleSparePartEntity> =
		mapList(input) { DtoMappers.toEntity(it) }
	
}