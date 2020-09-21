/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 20:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel.history

import com.mmdev.me.driver.core.utils.DateConverter
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.VehicleWithFuelHistory
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.model.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 *
 */

@RunWith(JUnit4::class)
class FuelHistoryMapperTest {
	
	private val mappers = FuelHistoryMappersFacade()
	
	private val vehicleDomain = Vehicle(
		"Ford",
		"Focus",
		2013,
		"vin",
		DistanceBound(kilometers = 2000, miles = null)
	)
	
	private val vehicleEntity = VehicleEntity(
		"Ford",
		"Focus",
		2013,
		"vin",
		DistanceBound(kilometers = 2000, miles = null)
	)
	
	private val entityList: List<FuelHistoryEntity> = listOf(
		FuelHistoryEntity(
			commentary = "",
			distancePassedBound = DistanceBound(kilometers = 400, miles = null),
			filledLiters = 0.0,
			fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
			fuelPrice = FuelPriceEntity("okko", 15.0, FuelType.A95.code),
			fuelStation = FuelStationEntity("OKKO", "okko", "01-01-2020"),
			odometerValueBound = DistanceBound(kilometers = 2000, miles = null),
			vehicleVinCode = vehicleEntity.vin,
			timestamp = DateConverter.toDate("12-01-2020").time
		)
	)
	
	private val vehicleWithFuelHistory = VehicleWithFuelHistory(
		vehicleEntity, entityList
	)
	
	private val domainList: List<FuelHistoryRecord> = listOf(
		FuelHistoryRecord(
			date = DateConverter.toDate("12-01-2020"),
			distancePassedBound = DistanceBound(kilometers = 400, miles = null),
			filledLiters = 0.0,
			fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
			fuelPrice = FuelPrice(15.0, FuelType.A95.code),
			fuelStation = FuelStation("OKKO", "okko", "01-01-2020"),
			vehicle = vehicleDomain
		)
	)
	
	
	@Test
	fun testMapDmHistoryToDb() {
		val mappingResult = mappers.mapDmHistoryToDb(domainList.first())
		assertTrue(mappingResult == entityList.first())
	}
	
	@Test
	fun testMapDbHistoryToDm() {
		val mappingResult = mappers.mapDbHistoryToDm(vehicleWithFuelHistory)
		assertTrue(mappingResult == domainList)
	}
}