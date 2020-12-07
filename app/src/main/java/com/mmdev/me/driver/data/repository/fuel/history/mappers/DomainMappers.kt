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

/**
 * In [FuelHistory] -> Out: [FuelHistoryEntity], [FuelHistoryDto]
 */

object DomainMappers {
	
	/** Out: [FuelHistoryEntity] */
	fun toEntity(domain: FuelHistory): FuelHistoryEntity =
		FuelHistoryEntity(
			commentary = domain.commentary,
			date = domain.date.toString(),
			dateAdded = domain.dateAdded,
			distancePassedBound = domain.distancePassedBound,
			filledLiters = domain.filledLiters,
			fuelConsumptionBound = domain.fuelConsumptionBound,
			fuelPrice = FuelPriceEmbedded(
				price = domain.fuelPrice.price,
				type = domain.fuelPrice.type.name
			),
			fuelStation = domain.fuelStation,
			moneySpent = domain.moneySpent,
			odometerValueBound = domain.odometerValueBound,
			vehicleVinCode = domain.vehicleVinCode
		)
	
	
	/** Out: [FuelHistoryDto] */
	fun toDto(domain: FuelHistory): FuelHistoryDto =
		FuelHistoryDto(
			commentary = domain.commentary,
			date = domain.date.toString(),
			dateAdded = domain.dateAdded,
			distancePassed = domain.distancePassedBound,
			filledLiters = domain.filledLiters,
			fuelConsumption = domain.fuelConsumptionBound,
			fuelPrice = domain.fuelPrice,
			fuelStation = domain.fuelStation,
			moneySpent = domain.moneySpent,
			odometerValue = domain.odometerValueBound,
			vehicleVinCode = domain.vehicleVinCode
		)
}