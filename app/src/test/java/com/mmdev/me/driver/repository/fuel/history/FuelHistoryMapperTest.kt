/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.11.2020 16:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel.history

import com.mmdev.me.driver.core.utils.currentEpochTime
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 *
 */

@RunWith(JUnit4::class)
class FuelHistoryMapperTest {
	
	private val mappers = FuelHistoryMappersFacade()
	
	private val time = currentEpochTime()
	
	
	private val historyList: List<FuelHistoryEntity> = listOf(
		FuelHistoryEntity(
			commentary = "",
			date = LocalDate(2020, 1, 15).atStartOfDayIn(
				TimeZone.currentSystemDefault()).toEpochMilliseconds(),
			dateAdded = time,
			distancePassedBound = DistanceBound(kilometers = 400, miles = null),
			filledLiters = 0.0,
			fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
			fuelPrice = FuelPriceEntity("okko", 15.0, FuelType.A95.code),
			fuelStation = FuelStationEntity("OKKO", "okko", "01-01-2020"),
			odometerValueBound = DistanceBound(kilometers = 2000, miles = null),
			vehicleVinCode = "vin",
		)
	)
	
	
	private val domainList: List<FuelHistory> = listOf(
		FuelHistory(
			date = LocalDate(2020, 1, 15).atStartOfDayIn(
				TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()),
			dateAdded = time,
			distancePassedBound = DistanceBound(kilometers = 400, miles = null),
			filledLiters = 0.0,
			fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
			fuelPrice = FuelPrice(15.0, FuelType.A95.code),
			fuelStation = FuelStation("OKKO", "okko", "01-01-2020"),
			odometerValueBound = DistanceBound(kilometers = 2000, miles = null),
			vehicleVinCode = "vin"
		)
	)
	
	
	@Test
	fun testMapDmHistoryToDb() {
		val mappingResult = mappers.domainToEntity(domainList.first())
		Assert.assertEquals(mappingResult, historyList.first())
	}
	
	@Test
	fun testMapDbHistoryToDm() {
		val mappingResult = mappers.listEntitiesToDomain(historyList)
		logWtf("mylogs", "${mappingResult.first().date.date}")
		Assert.assertEquals(mappingResult.first(), domainList.first())
	}
}