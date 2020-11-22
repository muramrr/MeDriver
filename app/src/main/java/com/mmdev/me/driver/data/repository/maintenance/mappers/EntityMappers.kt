/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 02:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.maintenance.mappers

import com.mmdev.me.driver.core.utils.convertToLocalDateTime
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.maintenance.remote.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildBy

/**
 * In [VehicleSparePartEntity] -> Out: [VehicleSparePart], [VehicleSparePartDto]
 */

object EntityMappers {
	
	fun toDomain(entity: VehicleSparePartEntity): VehicleSparePart =
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
	
	fun toDto(entity: VehicleSparePartEntity): VehicleSparePartDto =
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
			odometerValueBound = entity.odometerValueBound,
			vehicleVinCode = entity.vehicleVinCode
		)

}