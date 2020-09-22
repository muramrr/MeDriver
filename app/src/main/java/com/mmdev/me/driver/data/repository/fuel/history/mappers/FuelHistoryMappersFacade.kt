/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 00:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.VehicleWithFuelHistory
import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord

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
	
	fun apiDtoToDomain(dto: FuelHistoryDto): FuelHistoryRecord =
		FuelHistoryDtoMappers.apiDtoToDomain(dto)
	
	fun listApiDtosToDbEntities(dtoList: List<FuelHistoryDto>): List<FuelHistoryEntity> =
		mapList(dtoList) { apiDtoToDbEntity(it) }
	
	fun listApiDtosToDomains(dtoList: List<FuelHistoryDto>): List<FuelHistoryRecord> =
		mapList(dtoList) { apiDtoToDomain(it) }
	
	
	
	
	// in: domain, out: * entity, dto
	fun domainToDbEntity(domain: FuelHistoryRecord): FuelHistoryEntity =
		FuelHistoryDomainMappers.domainToDbEntity(domain)
	
	fun domainToApiDto(domain: FuelHistoryRecord): FuelHistoryDto =
		FuelHistoryDomainMappers.domainToApiDto(domain)
	
	
	
	
	// in: Db Entity, out: * domain, dto
	fun dbEntityToDomain(entity: FuelHistoryEntity): FuelHistoryRecord =
		FuelHistoryDbEntityMappers.dbEntityToDomain(entity)
	
	
	fun dbEntityToApiDto(entity: FuelHistoryEntity): FuelHistoryDto =
		FuelHistoryDbEntityMappers.dbEntityToApiDto(entity)
	
	fun listDbEntitiesToDomains(input: VehicleWithFuelHistory): List<FuelHistoryRecord> =
		mapList(input.fuelHistory) { dbEntityToDomain(it) }
	
	fun listDbEntitiesToApiDtos(input: VehicleWithFuelHistory): List<FuelHistoryDto> =
		mapList(input.fuelHistory) { dbEntityToApiDto(it) }
	
	
	
}