/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import java.util.*

/**
 * MappersFacade for multiple mappers used in FuelHistoryRepositoryImpl
 * contains mappers between layers [data -> domain]
 * mappers between sources [DomainModel -> dbEntity], [dbEntity -> DomainModel]
 *
 * DTO = Data Transfer Object
 * DB = database
 * DM = Domain Model
 */

class FuelHistoryMappersFacade {
	
	//dm -> db dto Single
	//////////////////////////////////////////////////////////////////////////////////////////////////
	val mapDmHistoryToDb: (FuelHistoryRecord) -> FuelHistoryEntity = { record ->
		FuelHistoryEntity(
			historyEntryId = record.id,
			commentary = record.commentary,
			timestamp = record.date.time,
			distancePassed = record.distancePassed,
			filledLiters = record.filledLiters,
			fuelConsumption = record.fuelConsumption,
			fuelPrice = FuelPriceEntity(
				record.fuelStation.slug,
				record.fuelPrice.price,
				record.fuelPrice.type.code
			),
			fuelStation = FuelStationEntity(
				record.fuelStation.brandTitle,
				record.fuelStation.slug,
				record.fuelStation.updatedDate
			),
			odometerValue = record.odometerValue
		)
	}
	
	//dm -> db dto Single
	//////////////////////////////////////////////////////////////////////////////////////////////////
	val mapDbHistoryToDm: (List<FuelHistoryEntity>) -> List<FuelHistoryRecord> = { input ->
		mapList(input) { entity ->
			FuelHistoryRecord(
				id = entity.historyEntryId,
				commentary = entity.commentary,
				date = Date(entity.timestamp),
				distancePassed = entity.distancePassed,
				filledLiters = entity.filledLiters,
				fuelConsumption = entity.fuelConsumption,
				fuelPrice = FuelPrice(
					price = entity.fuelPrice.price,
					type = entity.fuelPrice.type
				),
				fuelStation = FuelStation(
					brandTitle = entity.fuelStation.brandTitle,
					slug = entity.fuelStation.slug,
					updatedDate = entity.fuelStation.updatedDate
				),
				odometerValue = entity.odometerValue
			)
		}
	}
	
	
}