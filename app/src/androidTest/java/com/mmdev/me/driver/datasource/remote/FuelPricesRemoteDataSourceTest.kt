/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.datasource.remote

import com.mmdev.me.driver.FuelConstants
import com.mmdev.me.driver.data.datasource.fuel.prices.local.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPricesEntity
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
	
	
	private lateinit var fuelStationAndPricesOkko: FuelStationAndPricesEntity
	private lateinit var fuelStationAndPricesWog: FuelStationAndPricesEntity
	
	/**
	 * Override default Koin configuration to use Room in-memory database
	 */
	@Before
	fun before() {
		loadKoinModules(DatabaseTestModule)
		
		fuelStationAndPricesOkko = FuelStationAndPricesEntity(
			FuelConstants.fuelStationEntityOkko,
			listOf(FuelConstants.fuelPriceEntityOkko100, FuelConstants.fuelPriceEntityOkko95)
		)
		
		fuelStationAndPricesWog = FuelStationAndPricesEntity(
			FuelConstants.fuelStationEntityWog,
			listOf(FuelConstants.fuelPriceEntityWog100, FuelConstants.fuelPriceEntityWog95)
		)
	}
	
	
	@Test
	fun testAddFuelStationAndPrices() = runBlocking {
		
		dataSource.addFuelStation(FuelConstants.fuelStationEntityOkko)
		dataSource.addFuelStation(FuelConstants.fuelStationEntityWog)
		
		dataSource.addFuelPrice(FuelConstants.fuelPriceEntityOkko100)
		dataSource.addFuelPrice(FuelConstants.fuelPriceEntityOkko95)
		
		dataSource.addFuelPrice(FuelConstants.fuelPriceEntityWog100)
		dataSource.addFuelPrice(FuelConstants.fuelPriceEntityWog95)
		
		
		val result = dataSource.getFuelStationsAndPrices("01-01-2020")
		Assert.assertTrue(result is ResultState.Success)
		
		result.fold(
			success = {
				Assert.assertEquals(it, listOf(fuelStationAndPricesOkko, fuelStationAndPricesWog))
			},
			failure = {}
		)
		
	}
	
	@Test
	fun testDeleteFuelStationAndPrices() = runBlocking {
		
		val result = dataSource.getFuelStationsAndPrices("01-01-2020")
		Assert.assertTrue(result is ResultState.Success)
	
		dataSource.deleteAllFuelStation()
		
		val resultAfterDelete = dataSource.getFuelStationsAndPrices("01-01-2020")
		resultAfterDelete.fold(
			success = {
				Assert.assertEquals(emptyList<FuelStationAndPricesEntity>(), it)
			},
			failure = {}
		)
		
	}
	
	
}