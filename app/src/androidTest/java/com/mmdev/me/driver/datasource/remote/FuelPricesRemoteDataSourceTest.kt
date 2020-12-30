/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 17:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.datasource.remote

import com.mmdev.me.driver.FuelConstants
import com.mmdev.me.driver.data.datasource.fuel.prices.local.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.modules.DatabaseTestModule
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Tests for [IFuelPricesLocalDataSource]
 */

@RunWith(JUnit4::class)
class FuelPricesRemoteDataSourceTest: KoinTest {
	
	private val dataSource: IFuelPricesLocalDataSource by inject()
	
	
	private lateinit var fuelPrices: List<FuelPriceEntity>
	private lateinit var fuelStations: List<FuelStationEntity>
	
	
	/**
	 * Override default Koin configuration to use Room in-memory database
	 */
	@Before
	fun before() {
		loadKoinModules(DatabaseTestModule)
		
		fuelPrices = listOf(
			FuelConstants.fuelPriceEntityOkko100, FuelConstants.fuelPriceEntityOkko95,
			FuelConstants.fuelPriceEntityWog100, FuelConstants.fuelPriceEntityWog95
		)
		
		fuelStations = listOf(
			FuelConstants.fuelStationEntityWog, FuelConstants.fuelStationEntityOkko
		)
	}
	
	
	@Test
	fun testAddFuelStationAndPrices() = runBlocking {
		
		dataSource.addFuelStationsAndPrices(fuelStations, fuelPrices)
		
		
		val result = dataSource.getFuelStationsAndPrices("01-01-2020")
		Assert.assertTrue(result is ResultState.Success)
		
		result.fold(
			success = { Assert.assertEquals(it.size, 2) },
			failure = {}
		)
		
	}
	
	@Test
	fun testDeleteFuelStationAndPrices() = runBlocking {
		
		val result = dataSource.getFuelStationsAndPrices("01-01-2020")
		Assert.assertTrue(result is ResultState.Success)
	
		dataSource.deleteAllFuelStations()
		
		val resultAfterDelete = dataSource.getFuelStationsAndPrices("01-01-2020")
		resultAfterDelete.fold(
			success = { Assert.assertEquals(emptyList<FuelStationAndPrices>(), it) },
			failure = {}
		)
		
	}
	
	
}