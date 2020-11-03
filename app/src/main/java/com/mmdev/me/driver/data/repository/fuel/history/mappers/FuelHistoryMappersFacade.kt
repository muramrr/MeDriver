/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.11.2020 16:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory

/**
 * MappersFacade for multiple mappers used in FuelHistoryRepository
 * contains mappers between layers [data -> domain]
 *
 * DTO = Data Transfer Object (used only to store inside FirebaseFirestore)
 * ENTITY = Room database data class (annotation @Entity)
 * DM = Domain class
 */

class FuelHistoryMappersFacade {
	
	
	fun listDtoToEntities(dtoList: List<FuelHistoryDto>): List<FuelHistoryEntity> =
		mapList(dtoList) { DtoMappers.toEntity(it) }
	
	fun listDtoToDomain(dtoList: List<FuelHistoryDto>): List<FuelHistory> =
		mapList(dtoList) { DtoMappers.toDomain(it) }
	
	
	
	
	// in: domain, out: * entity, dto
	fun domainToEntity(domain: FuelHistory): FuelHistoryEntity = DomainMappers.toEntity(domain)
	
	fun domainToDto(domain: FuelHistory): FuelHistoryDto = DomainMappers.toDto(domain)
	
	
	
	
	// in: Db Entity, out: * domain, dto
	// in: Db Entity, out: * domain, dto
	fun entityToDomain(input: FuelHistoryEntity): FuelHistory = EntityMappers.toDomain(input)
	
	fun listEntitiesToDomain(input: List<FuelHistoryEntity>): List<FuelHistory> =
		mapList(input) { EntityMappers.toDomain(it) }
	
	fun listEntitiesToDto(input: List<FuelHistoryEntity>): List<FuelHistoryDto> =
		mapList(input) { EntityMappers.toDto(it) }
	
	
	
}