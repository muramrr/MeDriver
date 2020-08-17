/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.local.fuel.prices.entities.FuelStationEntity
import com.mmdev.me.driver.domain.fuel.FuelType.A100
import com.mmdev.me.driver.domain.fuel.FuelType.A95
import com.mmdev.me.driver.modules.roomTestModule
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
 * KoinTest help inject Koin components from actual runtime
 */

@RunWith(AndroidJUnit4::class)
class FuelDaoTest : KoinTest {

	/*
	 * Inject needed components from Koin
	 */
	private val fuelDao: FuelDao by inject()

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
		loadKoinModules(roomTestModule)
	}


	@Test
	fun testInsertFuelProvidersAndPrices() = runBlocking {
		
		
		// Create Fuel place entity
		val fuelProviderOkko =
			FuelStationEntity(
				"OKKO", "okko", "10-23-2330"
			)
		val fuelProviderWog =
			FuelStationEntity(
				"WOG", "wog", "10-23-2330"
			)
		
		val fuelOkko100 =
			FuelPriceEntity(
				"okko", 19.0, A100.code
			)
		
		val fuelOkko95 =
			FuelPriceEntity(
				"okko", 15.0, A95.code
			)
		
		val fuelWog100 =
			FuelPriceEntity(
				"wog", 21.0, A100.code
			)
		
		val fuelWog95 =
			FuelPriceEntity(
				"wog", 14.0, A95.code
			)
		
		val fuelProviderAndPricesOkko =
			FuelStationAndPrices(
				fuelProviderOkko, listOf(fuelOkko100, fuelOkko95)
			)
		
		val fuelProviderAndPricesWog =
			FuelStationAndPrices(
				fuelProviderWog, listOf(fuelWog100, fuelWog95)
			)
		
		
		fuelDao.insertFuelProvider(fuelProviderOkko)
		fuelDao.insertFuelProvider(fuelProviderWog)
		
		fuelDao.insertFuelPrice(fuelOkko100)
		fuelDao.insertFuelPrice(fuelOkko95)
		
		fuelDao.insertFuelPrice(fuelWog100)
		fuelDao.insertFuelPrice(fuelWog95)
		
		// Request
		val requestedEntities = fuelDao.getFuelPrices()
		
		// compare result
		assertTrue(requestedEntities.isNotEmpty())
		assertEquals(requestedEntities, listOf(fuelProviderAndPricesOkko, fuelProviderAndPricesWog))
		
		fuelDao.deleteAllFuelProviders()
		
		
		// compare result
		assertEquals(emptyList<FuelStationAndPrices>(), fuelDao.getFuelPrices())

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