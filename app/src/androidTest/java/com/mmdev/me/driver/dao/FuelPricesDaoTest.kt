/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mmdev.me.driver.FuelConstants.fuelPriceEntityOkko100
import com.mmdev.me.driver.FuelConstants.fuelPriceEntityOkko95
import com.mmdev.me.driver.FuelConstants.fuelPriceEntityWog100
import com.mmdev.me.driver.FuelConstants.fuelPriceEntityWog95
import com.mmdev.me.driver.FuelConstants.fuelStationEntityOkko
import com.mmdev.me.driver.FuelConstants.fuelStationEntityWog
import com.mmdev.me.driver.data.datasource.fuel.prices.local.dao.FuelPricesDao
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPricesEntity
import com.mmdev.me.driver.modules.DatabaseTestModule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Tests for [FuelPricesDao]
 */

@RunWith(AndroidJUnit4::class)
class FuelPricesDaoTest : KoinTest {

	/*
	 * Inject needed components from Koin
	 */
	private val dao: FuelPricesDao by inject()
	
	private lateinit var fuelStationAndPricesOkko: FuelStationAndPricesEntity
	private lateinit var fuelStationAndPricesWog: FuelStationAndPricesEntity
	
	
	

	//another approach without KoinTest
	//	private lateinit var fuelDatabase: MeDriveRoomDatabase //the db instance
	//	private lateinit var fuelDao: FuelDao //the dao
	//
	//	@Before
	//	fun setUp() {
	//		val context = ApplicationProvider.getApplicationContext<Context>()
	//		fuelDatabase = Room.inMemoryDatabaseBuilder(context, MeDriveRoomDatabase::class.java)
	//			.build()
	//
	//		fuelDao = fuelDatabase.getFuelDao()
	//	}

	
	
	
	/**
	 * Override default Koin configuration to use Room in-memory database
	 */
	@Before
	fun before() {
		loadKoinModules(DatabaseTestModule)
		
		fuelStationAndPricesOkko = FuelStationAndPricesEntity(
			fuelStationEntityOkko, listOf(fuelPriceEntityOkko100, fuelPriceEntityOkko95)
		)
		
		fuelStationAndPricesWog = FuelStationAndPricesEntity(
			fuelStationEntityWog, listOf(fuelPriceEntityWog100, fuelPriceEntityWog95)
		)
	}


	@Test
	fun testInsertFuelStationAndPrices() = runBlocking {
		
		dao.insertFuelStation(fuelStationEntityOkko)
		dao.insertFuelStation(fuelStationEntityWog)
		
		dao.insertFuelPrice(fuelPriceEntityOkko100)
		dao.insertFuelPrice(fuelPriceEntityOkko95)
		
		dao.insertFuelPrice(fuelPriceEntityWog100)
		dao.insertFuelPrice(fuelPriceEntityWog95)
		
		// Request
		val requestedEntities = dao.getFuelPrices("01-01-2020")
		
		// compare result
		assertTrue(requestedEntities.isNotEmpty())
		assertEquals(requestedEntities, listOf(fuelStationAndPricesOkko, fuelStationAndPricesOkko))

	}
	
	@Test
	fun testDeleteFuelStationAndPrices() = runBlocking {
		dao.insertFuelStation(fuelStationEntityOkko)
		dao.insertFuelStation(fuelStationEntityWog)
		
		dao.insertFuelPrice(fuelPriceEntityOkko100)
		dao.insertFuelPrice(fuelPriceEntityOkko95)
		
		dao.insertFuelPrice(fuelPriceEntityWog100)
		dao.insertFuelPrice(fuelPriceEntityWog95)
		
		// Request
		val requestedEntities = dao.getFuelPrices("01-01-2020")
		
		// compare result
		assertTrue(requestedEntities.isNotEmpty())
		assertEquals(requestedEntities, listOf(fuelStationAndPricesOkko, fuelStationAndPricesOkko))
		
		dao.deleteAllFuelStations()
		
		// compare result
		assertEquals(emptyList<FuelStationAndPricesEntity>(), dao.getFuelPrices("01-01-2020"))
	}



	/**
	 * Close resources
	 */
	@After
	fun after() {
		//fuelDatabase.close()
		//stopKoin()
	}
}