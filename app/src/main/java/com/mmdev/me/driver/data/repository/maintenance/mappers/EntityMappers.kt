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

import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.MaintenanceEntity
import com.mmdev.me.driver.data.datasource.maintenance.server.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildBy

/**
 * In [MaintenanceEntity] -> Out: [VehicleSparePart], [VehicleSparePartDto]
 */

object EntityMappers {
	
	fun toDomain(entity: MaintenanceEntity): VehicleSparePart =
		VehicleSparePart(
			date = convertToLocalDateTime(entity.date),
			dateAdded = entity.dateAdded,
			articulus = entity.articulus,
			vendor = entity.vendor,
			systemNode = VehicleSystemNodeType.valueOf(entity.systemNode),
			systemNodeComponent = VehicleSystemNodeType.valueOf(entity.systemNode).getChildBy(entity.systemNodeComponent),
			searchCriteria = entity.searchCriteria.split(", "),
			commentary = entity.commentary,
			moneySpent = entity.moneySpent,
			odometerValueBound = entity.odometerValueBound,
			vehicleVinCode = entity.vehicleVinCode
		)
	
	fun toDto(entity: MaintenanceEntity): VehicleSparePartDto =
		VehicleSparePartDto(
			date = convertToLocalDateTime(entity.date).toString(),
			dateAdded = entity.dateAdded,
			articulus = entity.articulus,
			vendor = entity.vendor,
			systemNode = entity.systemNode,
			systemNodeComponent = entity.systemNodeComponent,
			searchCriteria = entity.searchCriteria.split(", "),
			commentary = entity.commentary,
			moneySpent = entity.moneySpent,
			odometerValue = entity.odometerValueBound,
			vehicleVinCode = entity.vehicleVinCode
		)

}