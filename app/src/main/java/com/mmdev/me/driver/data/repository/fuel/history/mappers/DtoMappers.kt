/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 14:52
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
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

/**
 * In [FuelHistoryDto] -> Out: [FuelHistoryEntity], [FuelHistory]
 */

object DtoMappers {
	
	/** Out [FuelHistoryEntity] */
	fun toEntity(dto: FuelHistoryDto): FuelHistoryEntity =
		FuelHistoryEntity(
			commentary = dto.commentary,
			date = Instant.parse(dto.date).toEpochMilliseconds(),
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
				updatedDate = dto.fuelStation.updatedDate
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
			fuelPrice = FuelPrice(
				price = dto.fuelPrice.price,
				typeCode = dto.fuelPrice.type.code
			),
			fuelStation = FuelStation(
				brandTitle = dto.fuelStation.brandTitle,
				slug = dto.fuelStation.slug,
				updatedDate = dto.fuelStation.updatedDate
			),
			moneySpent = dto.moneySpent,
			odometerValueBound = dto.odometerValueBound,
			vehicleVinCode = dto.vehicleVinCode
		)
}