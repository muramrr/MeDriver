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