/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.11.2020 15:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.datasource.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mmdev.me.driver.FuelConstants
import com.mmdev.me.driver.core.utils.currentEpochTime
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.modules.DatabaseTestModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Tests for [IFuelHistoryLocalDataSource]
 */

@RunWith(AndroidJUnit4::class)
class FuelHistoryLocalDataSourceTest : KoinTest {
	

	private val dataSource: IFuelHistoryLocalDataSource by inject()
	
	private lateinit var fuelHistoryEntity1: FuelHistoryEntity
	private lateinit var fuelHistoryEntity2: FuelHistoryEntity
	private lateinit var fuelHistoryEntity3: FuelHistoryEntity
	
	private val time = currentEpochTime()
	/**
	 * Override test module
	 */
	@Before
	fun before() {
		loadKoinModules(DatabaseTestModule)
		
		//dataSource = FuelHistoryLocalDataSourceImpl(dao)
		
		fuelHistoryEntity1 = FuelHistoryEntity(
			commentary = "",
			date = time,
			dateAdded = time,
			distancePassedBound = DistanceBound(kilometers = 400, miles = null),
			filledLiters = 0.0,
			fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
			fuelPrice = FuelConstants.fuelPriceEntityWog100,
			fuelStation = FuelConstants.fuelStationEntityWog,
			moneySpent = 300.0,
			odometerValueBound = DistanceBound(kilometers = 1000, miles = null),
			vehicleVinCode = "someVin",
		)
		fuelHistoryEntity2 = FuelHistoryEntity(
			commentary = "",
			date = time,
			dateAdded = time,
			distancePassedBound = DistanceBound(kilometers = 500, miles = null),
			filledLiters = 0.0,
			fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
			fuelPrice = FuelConstants.fuelPriceEntityWog95,
			fuelStation = FuelConstants.fuelStationEntityWog,
			moneySpent = 300.0,
			odometerValueBound = DistanceBound(kilometers = 1500, miles = null),
			vehicleVinCode = "someVin",
		)
		fuelHistoryEntity3 = FuelHistoryEntity(
			commentary = "",
			date = time,
			dateAdded = time,
			distancePassedBound = DistanceBound(kilometers = 500, miles = null),
			filledLiters = 0.0,
			fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
			fuelPrice = FuelConstants.fuelPriceEntityWog95,
			fuelStation = FuelConstants.fuelStationEntityWog,
			moneySpent = 300.0,
			odometerValueBound = DistanceBound(kilometers = 2000, miles = null),
			vehicleVinCode = "someVin",
		)
	}
	
	
	@Test
	fun testInsertFuelHistoryEntity() = runBlocking {
		dataSource.insertFuelHistoryEntry(fuelHistoryEntity1)
		dataSource.insertFuelHistoryEntry(fuelHistoryEntity2)
		dataSource.insertFuelHistoryEntry(fuelHistoryEntity3)
		
		// Request
		val result = dataSource.getFuelHistory("someVin",1, 0)
		
		// compare result
		Assert.assertTrue(result is ResultState.Success)
		result.fold(
			success = { Assert.assertEquals(it, listOf(fuelHistoryEntity1)) },
			failure = {}
		)
		
	}
	
	
	
	@Test
	fun testDeleteFuelHistoryEntity() = runBlocking {
		// Request
		val result = dataSource.getFuelHistory("someVin", 1, 0)
		
		// compare result
		Assert.assertTrue(result is ResultState.Success)
		result.fold(
			success = { Assert.assertEquals(it, listOf(fuelHistoryEntity1)) },
			failure = {}
		)
		delay(50)
		
		val deletionResult = dataSource.deleteFuelHistoryEntry(fuelHistoryEntity1)
		Assert.assertTrue(deletionResult is ResultState.Success)
		
		val afterDeletionResult = dataSource.getFuelHistory("someVin", 10, 0)
		Assert.assertTrue(deletionResult is ResultState.Success)
		
		afterDeletionResult.fold(
			success = {
				Assert.assertTrue(it.isNotEmpty())
			},
			failure = {}
		)
		
	}

}