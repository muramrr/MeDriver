/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 21:28
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.maintenance.mappers

import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 *
 */

object DomainMappers {
	
	fun toEntity(domain: VehicleSparePart): VehicleSparePartEntity =
		VehicleSparePartEntity(
			date = domain.date.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
			dateAdded = domain.dateAdded,
			articulus = domain.articulus,
			vendor = domain.vendor,
			systemNode = domain.systemNode.name,
			systemNodeComponent = domain.systemNodeComponent.getSparePartName(),
			customNodeComponent = domain.customNodeComponent,
			commentary = domain.commentary,
			moneySpent = domain.moneySpent,
			odometerValueBound = domain.odometerValueBound,
			vehicleVinCode = domain.vehicleVinCode
		)
}