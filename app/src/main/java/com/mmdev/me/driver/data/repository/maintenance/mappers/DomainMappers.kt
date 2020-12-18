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

/**
 * In [VehicleSparePart] -> Out: [MaintenanceEntity], [VehicleSparePartDto]
 */

object DomainMappers {
	
	fun toEntity(domain: VehicleSparePart): MaintenanceEntity =
		MaintenanceEntity(
			date = domain.date.toEpochTime(),
			dateAdded = domain.dateAdded,
			articulus = domain.articulus,
			vendor = domain.vendor,
			systemNode = domain.systemNode.name,
			systemNodeComponent = domain.systemNodeComponent.getSparePartName(),
			searchCriteria = domain.searchCriteria.joinToString(),
			commentary = domain.commentary,
			moneySpent = domain.moneySpent,
			odometerValueBound = domain.odometerValueBound,
			vehicleVinCode = domain.vehicleVinCode
		)
	
	
	fun toDto(domain: VehicleSparePart): VehicleSparePartDto =
		VehicleSparePartDto(
			date = domain.date.toString(),
			dateAdded = domain.dateAdded,
			articulus = domain.articulus,
			vendor = domain.vendor,
			systemNode = domain.systemNode.name,
			systemNodeComponent = domain.systemNodeComponent.getSparePartName(),
			searchCriteria = domain.searchCriteria,
			commentary = domain.commentary,
			moneySpent = domain.moneySpent,
			odometerValue = domain.odometerValueBound,
			vehicleVinCode = domain.vehicleVinCode
		)
}