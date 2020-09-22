/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 00:56
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
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation

/**
 * In [FuelHistoryRecord] -> Out [FuelHistoryEntity], [FuelHistoryDto]
 */

object FuelHistoryDomainMappers {
	
	/** Out: [FuelHistoryEntity] */
	fun domainToDbEntity(domain: FuelHistoryRecord): FuelHistoryEntity =
		FuelHistoryEntity(
			commentary = domain.commentary,
			timestamp = domain.date.time,
			distancePassedBound = domain.distancePassedBound,
			filledLiters = domain.filledLiters,
			fuelConsumptionBound = domain.fuelConsumptionBound,
			fuelPrice = FuelPriceEntity(
				fuelStationId = domain.fuelStation.slug,
				price = domain.fuelPrice.price,
				type = domain.fuelPrice.type.code
			),
			fuelStation = FuelStationEntity(
				brandTitle = domain.fuelStation.brandTitle,
				slug = domain.fuelStation.slug,
				updatedDate = domain.fuelStation.updatedDate
			),
			odometerValueBound = domain.odometerValueBound,
			vehicleVinCode = domain.vehicleVinCode
		)
	
	
	/** Out: [FuelHistoryDto] */
	fun domainToApiDto(domain: FuelHistoryRecord): FuelHistoryDto =
		FuelHistoryDto(
			commentary = domain.commentary,
			date = domain.date,
			distancePassedBound = domain.distancePassedBound,
			filledLiters = domain.filledLiters,
			fuelConsumptionBound = domain.fuelConsumptionBound,
			fuelPrice = FuelPrice(
				price = domain.fuelPrice.price,
				type = domain.fuelPrice.type.code
			),
			fuelStation = FuelStation(
				brandTitle = domain.fuelStation.brandTitle,
				slug = domain.fuelStation.slug,
				updatedDate = domain.fuelStation.updatedDate
			),
			odometerValueBound = domain.odometerValueBound,
			vehicleVinCode = domain.vehicleVinCode
		)
}