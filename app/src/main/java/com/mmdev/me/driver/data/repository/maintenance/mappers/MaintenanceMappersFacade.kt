/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 14:52
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
 * MappersFacade for multiple mappers used in MaintenanceRepository
 * contains mappers between layers [data -> domain]
 *
 * DTO = Data Transfer Object (used only to store inside FirebaseFirestore)
 * ENTITY = Room database data class (annotation @Entity)
 * DM = Domain class
 */

class MaintenanceMappersFacade {
	
	
	// in: entities, out: * dto, domain
	fun listEntitiesToDomains(input: List<VehicleSparePartEntity>): List<VehicleSparePart> =
		mapList(input) { EntityMappers.toDomain(it) }
	
	fun listEntitiesToDto(input: List<VehicleSparePartEntity>): List<VehicleSparePartDto> =
		mapList(input) { EntityMappers.toDto(it) }
	
	
	// in: domain, out: * dto, entity
	fun listDomainsToEntities(input: List<VehicleSparePart>): List<VehicleSparePartEntity> =
		mapList(input) { DomainMappers.toEntity(it) }
	
	fun listDomainsToDto(input: List<VehicleSparePart>): List<VehicleSparePartDto> =
		mapList(input) { DomainMappers.toDto(it) }
	
	
	// in: dto, out: * entities, domain
	fun listDtoToDomains(input: List<VehicleSparePartDto>): List<VehicleSparePart> =
		mapList(input) { DtoMappers.toDomain(it) }
	
	fun listDtoToEntities(input: List<VehicleSparePartDto>): List<VehicleSparePartEntity> =
		mapList(input) { DtoMappers.toEntity(it) }
	
}