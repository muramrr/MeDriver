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

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.server.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory

/**
 * Mapping between FuelHistory DTOs/entities/domain data classes
 */

class FuelHistoryMappersFacade {
	
	fun dtoToEntity(dto: FuelHistoryDto): FuelHistoryEntity =
		DtoMappers.toEntity(dto)
	fun listDtoToEntities(dtoList: List<FuelHistoryDto>): List<FuelHistoryEntity> =
		mapList(dtoList) { DtoMappers.toEntity(it) }
	
	fun listDtoToDomain(dtoList: List<FuelHistoryDto>): List<FuelHistory> =
		mapList(dtoList) { DtoMappers.toDomain(it) }
	
	
	
	
	// in: domain, out: * entity, dto
	fun domainToEntity(domain: FuelHistory): FuelHistoryEntity = DomainMappers.toEntity(domain)
	fun domainToDto(domain: FuelHistory): FuelHistoryDto = DomainMappers.toDto(domain)
	fun listDomainToEntities(input: List<FuelHistory>): List<FuelHistoryEntity> = mapList(input) {
		DomainMappers.toEntity(it)
	}
	
	
	
	
	// in: Db Entity, out: * domain, dto
	fun entityToDomain(entity: FuelHistoryEntity): FuelHistory = EntityMappers.toDomain(entity)
	fun entityToDto(entity: FuelHistoryEntity): FuelHistoryDto = EntityMappers.toDto(entity)
	
	fun listEntitiesToDomain(input: List<FuelHistoryEntity>): List<FuelHistory> =
		mapList(input) { EntityMappers.toDomain(it) }
	
	fun listEntitiesToDto(input: List<FuelHistoryEntity>): List<FuelHistoryDto> =
		mapList(input) { EntityMappers.toDto(it) }
	
	
	
}