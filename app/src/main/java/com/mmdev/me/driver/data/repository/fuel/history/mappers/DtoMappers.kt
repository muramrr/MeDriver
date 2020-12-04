/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 21:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelPriceEmbedded
import com.mmdev.me.driver.data.datasource.fuel.history.server.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import kotlinx.datetime.LocalDateTime

/**
 * In [FuelHistoryDto] -> Out: [FuelHistoryEntity], [FuelHistory]
 */

object DtoMappers {
	
	/** Out [FuelHistoryEntity] */
	fun toEntity(dto: FuelHistoryDto): FuelHistoryEntity =
		FuelHistoryEntity(
			commentary = dto.commentary,
			date = dto.date,
			dateAdded = dto.dateAdded,
			distancePassedBound = dto.distancePassed,
			filledLiters = dto.filledLiters,
			fuelConsumptionBound = dto.fuelConsumption,
			fuelPrice = FuelPriceEmbedded(
				price = dto.fuelPrice.price,
				type = dto.fuelPrice.type.name
			),
			fuelStation = dto.fuelStation,
			moneySpent = dto.moneySpent,
			odometerValueBound = dto.odometerValue,
			vehicleVinCode = dto.vehicleVinCode
		)
	
	/** Out [FuelHistory] */
	fun toDomain(dto: FuelHistoryDto): FuelHistory =
		FuelHistory(
			commentary = dto.commentary,
			date = LocalDateTime.parse(dto.date),
			dateAdded = dto.dateAdded,
			distancePassedBound = dto.distancePassed,
			filledLiters = dto.filledLiters,
			fuelConsumptionBound = dto.fuelConsumption,
			fuelPrice = dto.fuelPrice,
			fuelStation = dto.fuelStation,
			moneySpent = dto.moneySpent,
			odometerValueBound = dto.odometerValue,
			vehicleVinCode = dto.vehicleVinCode
		)
}