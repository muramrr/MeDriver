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
import com.mmdev.me.driver.data.datasource.fuel.history.server.dto.FuelHistoryDto
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import kotlinx.datetime.LocalDateTime

/**
 * In [FuelHistoryEntity] -> Out [FuelHistory], [FuelHistoryDto]
 */

object EntityMappers {
	
	/** Out: [FuelHistory] */
	fun toDomain(entity: FuelHistoryEntity): FuelHistory =
		FuelHistory(
			commentary = entity.commentary,
			date = LocalDateTime.parse(entity.date),
			dateAdded = entity.dateAdded,
			distancePassedBound = entity.distancePassedBound,
			filledLiters = entity.filledLiters,
			fuelConsumptionBound = entity.fuelConsumptionBound,
			fuelPrice = FuelPrice(
				price = entity.fuelPrice.price,
				type = FuelType.valueOf(entity.fuelPrice.type)
			),
			fuelStation = FuelStation(
				brandTitle = entity.fuelStation.brandTitle,
				slug = entity.fuelStation.slug,
				updatedDate = entity.fuelStation.updatedDate
			),
			moneySpent = entity.moneySpent,
			odometerValueBound = entity.odometerValueBound,
			vehicleVinCode = entity.vehicleVinCode
		)
	
	/** Out: [FuelHistoryDto] */
	fun toDto(entity: FuelHistoryEntity): FuelHistoryDto =
		FuelHistoryDto(
			commentary = entity.commentary,
			date = entity.date,
			dateAdded = entity.dateAdded,
			distancePassed = entity.distancePassedBound,
			filledLiters = entity.filledLiters,
			fuelConsumption = entity.fuelConsumptionBound,
			fuelPrice = FuelPrice(
				price = entity.fuelPrice.price,
				type = FuelType.valueOf(entity.fuelPrice.type)
			),
			fuelStation = FuelStation(
				brandTitle = entity.fuelStation.brandTitle,
				slug = entity.fuelStation.slug,
				updatedDate = entity.fuelStation.updatedDate
			),
			moneySpent = entity.moneySpent,
			odometerValue = entity.odometerValueBound,
			vehicleVinCode = entity.vehicleVinCode
		)
	
}