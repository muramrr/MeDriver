/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.maintenance.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.maintenance.remote.dto.VehicleSparePartDto
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