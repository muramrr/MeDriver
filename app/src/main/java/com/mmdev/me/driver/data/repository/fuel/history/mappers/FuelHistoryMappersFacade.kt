/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.10.2020 17:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistory

/**
 * MappersFacade for multiple mappers used in FuelHistoryRepository
 * contains mappers between layers [data -> domain]
 *
 * DTO = Data Transfer Object
 * DB = database
 * DM = Domain Model
 */

class FuelHistoryMappersFacade {
	
	// in: dto, out: * domain, entity
	fun apiDtoToDbEntity(dto: FuelHistoryDto): FuelHistoryEntity =
		FuelHistoryDtoMappers.apiDtoToDbEntity(dto)
	
	fun apiDtoToDomain(dto: FuelHistoryDto): FuelHistory =
		FuelHistoryDtoMappers.apiDtoToDomain(dto)
	
	fun listApiDtosToDbEntities(dtoList: List<FuelHistoryDto>): List<FuelHistoryEntity> =
		mapList(dtoList) { apiDtoToDbEntity(it) }
	
	fun listApiDtosToDomains(dtoList: List<FuelHistoryDto>): List<FuelHistory> =
		mapList(dtoList) { apiDtoToDomain(it) }
	
	
	
	
	// in: domain, out: * entity, dto
	fun domainToDbEntity(domain: FuelHistory): FuelHistoryEntity =
		FuelHistoryDomainMappers.domainToDbEntity(domain)
	
	fun domainToApiDto(domain: FuelHistory): FuelHistoryDto =
		FuelHistoryDomainMappers.domainToApiDto(domain)
	
	
	
	
	// in: Db Entity, out: * domain, dto
	fun dbEntityToDomain(entity: FuelHistoryEntity): FuelHistory =
		FuelHistoryDbEntityMappers.dbEntityToDomain(entity)
	
	
	fun dbEntityToApiDto(entity: FuelHistoryEntity): FuelHistoryDto =
		FuelHistoryDbEntityMappers.dbEntityToApiDto(entity)
	
	fun listDbEntitiesToDomains(input: List<FuelHistoryEntity>): List<FuelHistory> =
		mapList(input) { dbEntityToDomain(it) }
	
	fun listDbEntitiesToApiDtos(input: List<FuelHistoryEntity>): List<FuelHistoryDto> =
		mapList(input) { dbEntityToApiDto(it) }
	
	
	
}