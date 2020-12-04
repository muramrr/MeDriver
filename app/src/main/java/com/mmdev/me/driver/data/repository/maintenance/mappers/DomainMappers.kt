/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 21:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.maintenance.mappers

import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.maintenance.server.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart

/**
 * In [VehicleSparePart] -> Out: [VehicleSparePartEntity], [VehicleSparePartDto]
 */

object DomainMappers {
	
	fun toEntity(domain: VehicleSparePart): VehicleSparePartEntity =
		VehicleSparePartEntity(
			date = domain.date.toString(),
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