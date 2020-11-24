/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.11.2020 19:47
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * In [FuelHistoryDto] -> Out: [FuelHistoryEntity], [FuelHistory]
 */

object DtoMappers {
	
	/** Out [FuelHistoryEntity] */
	fun toEntity(dto: FuelHistoryDto): FuelHistoryEntity =
		FuelHistoryEntity(
			commentary = dto.commentary,
			date = LocalDateTime.parse(dto.date).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
			dateAdded = dto.dateAdded,
			distancePassedBound = dto.distancePassedBound,
			filledLiters = dto.filledLiters,
			fuelConsumptionBound = dto.fuelConsumptionBound,
			fuelPrice = FuelPriceEntity(
				fuelStationId = dto.fuelStation.slug,
				price = dto.fuelPrice.price,
				typeCode = dto.fuelPrice.type.code
			),
			fuelStation = FuelStationEntity(
				brandTitle = dto.fuelStation.brandTitle,
				slug = dto.fuelStation.slug,
				updatedDate = dto.fuelStation.updatedDate,
				regionId = dto.fuelStation.regionId
			),
			moneySpent = dto.moneySpent,
			odometerValueBound = dto.odometerValueBound,
			vehicleVinCode = dto.vehicleVinCode
		)
	
	/** Out [FuelHistory] */
	fun toDomain(dto: FuelHistoryDto): FuelHistory =
		FuelHistory(
			commentary = dto.commentary,
			date = LocalDateTime.parse(dto.date),
			dateAdded = dto.dateAdded,
			distancePassedBound = dto.distancePassedBound,
			filledLiters = dto.filledLiters,
			fuelConsumptionBound = dto.fuelConsumptionBound,
			fuelPrice = dto.fuelPrice,
			fuelStation = dto.fuelStation,
			moneySpent = dto.moneySpent,
			odometerValueBound = dto.odometerValueBound,
			vehicleVinCode = dto.vehicleVinCode
		)
}