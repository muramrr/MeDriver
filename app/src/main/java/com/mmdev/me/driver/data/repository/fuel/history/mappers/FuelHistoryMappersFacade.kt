/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 20:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.VehicleWithFuelHistory
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
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
			commentary = record.commentary,
			timestamp = record.date.time,
			distancePassedBound = record.distancePassedBound,
			filledLiters = record.filledLiters,
			fuelConsumptionBound = record.fuelConsumptionBound,
			fuelPrice = FuelPriceEntity(
				fuelStationId = record.fuelStation.slug,
				price = record.fuelPrice.price,
				type = record.fuelPrice.type.code
			),
			fuelStation = FuelStationEntity(
				brandTitle = record.fuelStation.brandTitle,
				slug = record.fuelStation.slug,
				updatedDate = record.fuelStation.updatedDate
			),
			odometerValueBound = record.odometerValueBound,
			vehicleVinCode = record.vehicle.vin
		)
	}
	
	//dm -> db dto Single
	//////////////////////////////////////////////////////////////////////////////////////////////////
	val mapDbHistoryToDm: (VehicleWithFuelHistory) -> List<FuelHistoryRecord> = { input ->
		mapList(input.fuelHistory) { historyEntity ->
			FuelHistoryRecord(
				commentary = historyEntity.commentary,
				date = Date(historyEntity.timestamp),
				distancePassedBound = historyEntity.distancePassedBound,
				filledLiters = historyEntity.filledLiters,
				fuelConsumptionBound = historyEntity.fuelConsumptionBound,
				fuelPrice = FuelPrice(
					price = historyEntity.fuelPrice.price,
					type = historyEntity.fuelPrice.type
				),
				fuelStation = FuelStation(
					brandTitle = historyEntity.fuelStation.brandTitle,
					slug = historyEntity.fuelStation.slug,
					updatedDate = historyEntity.fuelStation.updatedDate
				),
				odometerValueBound = historyEntity.odometerValueBound,
				vehicle = Vehicle(
					brand = input.vehicleEntity.brand,
					model = input.vehicleEntity.model,
					year = input.vehicleEntity.year,
					vin = input.vehicleEntity.vin,
					odometerValueBound = input.vehicleEntity.odometerValueBound
				)
			)
		}
	}
	
	
}