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

import com.mmdev.me.driver.core.utils.extensions.toEpochTime
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.MaintenanceEntity
import com.mmdev.me.driver.data.datasource.maintenance.server.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildBy
import kotlinx.datetime.LocalDateTime

/**
 * In [VehicleSparePartDto] -> Out: [VehicleSparePart], [MaintenanceEntity]
 */

object DtoMappers {
	
	fun toDomain(dto: VehicleSparePartDto): VehicleSparePart =
		VehicleSparePart(
			date = LocalDateTime.parse(dto.date),
			dateAdded = dto.dateAdded,
			articulus = dto.articulus,
			vendor = dto.vendor,
			systemNode = VehicleSystemNodeType.valueOf(dto.systemNode),
			systemNodeComponent = VehicleSystemNodeType.valueOf(dto.systemNode).getChildBy(dto.systemNodeComponent),
			searchCriteria = dto.searchCriteria,
			commentary = dto.commentary,
			moneySpent = dto.moneySpent,
			odometerValueBound = dto.odometerValue,
			vehicleVinCode = dto.vehicleVinCode
		)
	
	
	fun toEntity(dto: VehicleSparePartDto): MaintenanceEntity =
		MaintenanceEntity(
			date = LocalDateTime.parse(dto.date).toEpochTime(),
			dateAdded = dto.dateAdded,
			articulus = dto.articulus,
			vendor = dto.vendor,
			systemNode = dto.systemNode,
			systemNodeComponent = dto.systemNodeComponent,
			searchCriteria = dto.searchCriteria.joinToString(),
			commentary = dto.commentary,
			moneySpent = dto.moneySpent,
			odometerValueBound = dto.odometerValue,
			vehicleVinCode = dto.vehicleVinCode
		)
	
}