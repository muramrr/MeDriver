/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 18:10
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.vehicle.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.remote.dto.VehicleDto
import com.mmdev.me.driver.domain.vehicle.model.Vehicle

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
	
	fun listApiDtoToDbEntity(input: List<VehicleDto>): List<VehicleEntity> = mapList(input) {
		single -> apiDtoToDbEntity(single)
	}
	
	fun listDbEntityToDomain(input: List<VehicleEntity>): List<Vehicle> = mapList(input) {
		single -> dbEntityToDomain(single)
	}
	
	fun listApiDtoToDomain(input: List<VehicleDto>): List<Vehicle> = mapList(input) {
		single -> apiDtoToDomain(single)
	}
	
	fun apiDtoToDomain(dto: VehicleDto): Vehicle = Vehicle(
		brand = dto.brand,
		model = dto.model,
		year = dto.year,
		vin = dto.vin,
		odometerValueBound = dto.odometerValueBound
	)
	
	fun apiDtoToDbEntity(dto: VehicleDto): VehicleEntity = VehicleEntity(
		brand = dto.brand,
		model = dto.model,
		year = dto.year,
		vin = dto.vin,
		odometerValueBound = dto.odometerValueBound
	)
	
	fun dbEntityToApiDto(entity: VehicleEntity): VehicleDto = VehicleDto(
		brand = entity.brand,
		model = entity.model,
		year = entity.year,
		vin = entity.vin,
		odometerValueBound = entity.odometerValueBound
	)
	
	fun dbEntityToDomain(dto: VehicleEntity): Vehicle = Vehicle(
		brand = dto.brand,
		model = dto.model,
		year = dto.year,
		vin = dto.vin,
		odometerValueBound = dto.odometerValueBound
	)
	
	fun domainToDbEntity(domain: Vehicle): VehicleEntity = VehicleEntity(
		brand = domain.brand,
		model = domain.model,
		year = domain.year,
		vin = domain.vin,
		odometerValueBound = domain.odometerValueBound
	)
	
	fun domainToApiDto(domain: Vehicle): VehicleDto = VehicleDto(
		brand = domain.brand,
		model = domain.model,
		year = domain.year,
		vin = domain.vin,
		odometerValueBound = domain.odometerValueBound
	)
	
	
}
