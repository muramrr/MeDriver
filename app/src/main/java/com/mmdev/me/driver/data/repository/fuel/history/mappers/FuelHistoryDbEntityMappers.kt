/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 01:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import java.util.*

/**
 * In [FuelHistoryEntity] -> Out [FuelHistoryRecord], [FuelHistoryDto]
 */

object FuelHistoryDbEntityMappers {
	
	/** Out: [FuelHistoryRecord] */
	fun dbEntityToDomain(entity: FuelHistoryEntity): FuelHistoryRecord =
		FuelHistoryRecord(
			commentary = entity.commentary,
			date = Date(entity.timestamp),
			distancePassedBound = entity.distancePassedBound,
			filledLiters = entity.filledLiters,
			fuelConsumptionBound = entity.fuelConsumptionBound,
			fuelPrice = FuelPrice(
				price = entity.fuelPrice.price,
				type = entity.fuelPrice.type
			),
			fuelStation = FuelStation(
				brandTitle = entity.fuelStation.brandTitle,
				slug = entity.fuelStation.slug,
				updatedDate = entity.fuelStation.updatedDate
			),
			odometerValueBound = entity.odometerValueBound,
			vehicleVinCode = entity.vehicleVinCode
		)
	
	/** Out: [FuelHistoryDto] */
	fun dbEntityToApiDto(entity: FuelHistoryEntity): FuelHistoryDto =
		FuelHistoryDto(
			commentary = entity.commentary,
			date = Date(entity.timestamp),
			distancePassedBound = entity.distancePassedBound,
			filledLiters = entity.filledLiters,
			fuelConsumptionBound = entity.fuelConsumptionBound,
			fuelPrice = FuelPrice(
				price = entity.fuelPrice.price,
				type = entity.fuelPrice.type
			),
			fuelStation = FuelStation(
				brandTitle = entity.fuelStation.brandTitle,
				slug = entity.fuelStation.slug,
				updatedDate = entity.fuelStation.updatedDate
			),
			odometerValueBound = entity.odometerValueBound,
			vehicleVinCode = entity.vehicleVinCode
		)
	
}