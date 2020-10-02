/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.10.2020 16:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.core.utils.convertToLocalDateTime
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistory
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation

/**
 * In [FuelHistoryEntity] -> Out [FuelHistory], [FuelHistoryDto]
 */

object FuelHistoryDbEntityMappers {
	
	/** Out: [FuelHistory] */
	fun dbEntityToDomain(entity: FuelHistoryEntity): FuelHistory =
		FuelHistory(
			commentary = entity.commentary,
			date = convertToLocalDateTime(entity.date),
			dateAdded = entity.dateAdded,
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
			date = convertToLocalDateTime(entity.date).toString(),
			dateAdded = entity.dateAdded,
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