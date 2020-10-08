/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 21:33
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
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toInstant

/**
 * In [FuelHistory] -> Out [FuelHistoryEntity], [FuelHistoryDto]
 */

object DomainMappers {
	
	/** Out: [FuelHistoryEntity] */
	fun toEntity(domain: FuelHistory): FuelHistoryEntity =
		FuelHistoryEntity(
			commentary = domain.commentary,
			date = domain.date.toInstant(currentSystemDefault()).toEpochMilliseconds(),
			dateAdded = domain.dateAdded,
			distancePassedBound = domain.distancePassedBound,
			filledLiters = domain.filledLiters,
			fuelConsumptionBound = domain.fuelConsumptionBound,
			fuelPrice = FuelPriceEntity(
				fuelStationId = domain.fuelStation.slug,
				price = domain.fuelPrice.price,
				typeCode = domain.fuelPrice.type.code
			),
			fuelStation = FuelStationEntity(
				brandTitle = domain.fuelStation.brandTitle,
				slug = domain.fuelStation.slug,
				updatedDate = domain.fuelStation.updatedDate
			),
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
			distancePassedBound = domain.distancePassedBound,
			filledLiters = domain.filledLiters,
			fuelConsumptionBound = domain.fuelConsumptionBound,
			fuelPrice = FuelPrice(
				price = domain.fuelPrice.price,
				typeCode = domain.fuelPrice.type.code
			),
			fuelStation = FuelStation(
				brandTitle = domain.fuelStation.brandTitle,
				slug = domain.fuelStation.slug,
				updatedDate = domain.fuelStation.updatedDate
			),
			moneySpent = domain.moneySpent,
			odometerValueBound = domain.odometerValueBound,
			vehicleVinCode = domain.vehicleVinCode
		)
}