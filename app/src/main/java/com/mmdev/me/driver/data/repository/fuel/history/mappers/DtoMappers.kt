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