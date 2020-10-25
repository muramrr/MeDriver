/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.10.2020 19:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.maintenance.mappers

import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.maintenance.remote.dto.VehicleSparePartDto
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildBy
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

/**
 * In [VehicleSparePartDto] -> Out: [VehicleSparePart], [VehicleSparePartEntity]
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
			odometerValueBound = dto.odometerValueBound,
			vehicleVinCode = dto.vehicleVinCode
		)
	
	
	fun toEntity(dto: VehicleSparePartDto): VehicleSparePartEntity =
		VehicleSparePartEntity(
			date = Instant.parse(dto.date).toEpochMilliseconds(),
			dateAdded = dto.dateAdded,
			articulus = dto.articulus,
			vendor = dto.vendor,
			systemNode = dto.systemNode,
			systemNodeComponent = dto.systemNodeComponent,
			searchCriteria = dto.searchCriteria.toString(),
			commentary = dto.commentary,
			moneySpent = dto.moneySpent,
			odometerValueBound = dto.odometerValueBound,
			vehicleVinCode = dto.vehicleVinCode
		)
	
}