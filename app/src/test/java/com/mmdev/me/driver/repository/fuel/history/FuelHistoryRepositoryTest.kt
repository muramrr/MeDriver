/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel.history

import com.mmdev.me.driver.core.utils.DateConverter
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.repository.fuel.history.FuelHistoryRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.FuelType.A95
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Tests for [IFuelHistoryRepository]
 */

@RunWith(JUnit4::class)
class FuelHistoryRepositoryTest {
	
	private val localDataSource: IFuelHistoryLocalDataSource = mockk()
	private val mappers = FuelHistoryMappersFacade()
	
	private val repository: IFuelHistoryRepository = FuelHistoryRepositoryImpl(localDataSource, mappers)
	
	private val validLimit = 20
	private val validOffset = 0
	private val invalidLimit = 1
	
	
	private val validReturn = listOf(
		FuelHistoryEntity(
			historyEntryId = 1,
			commentary = "",
			distancePassed = 400,
			filledLiters = 0.0,
			fuelConsumption = 0.0,
			fuelPrice = FuelPriceEntity("okko", 15.0, A95.code),
			fuelStation = FuelStationEntity("OKKO", "okko", "01-01-2020"),
			odometerValue = 2000,
			timestamp = DateConverter.toDate("12-01-2020").time
		)
	)
	
	@Before
	fun before() {
		coEvery {
			localDataSource.getFuelHistory(validLimit, validOffset)
		} returns ResultState.Success(validReturn)
		
		coEvery {
			localDataSource.getFuelHistory(invalidLimit, validOffset)
		} returns ResultState.Failure(Exception("Invalid"))
	}
	
	@Test
	fun testSuccessfulReturnFromLocalDataSource() = runBlocking {
		
		val result = repository.loadFuelHistory(null)
	
		result.fold(
			success = { Assert.assertTrue(it.isNotEmpty()) },
			failure = { }
		)
		
		Assert.assertTrue(result is ResultState.Success)
		
	}
	
	@Test
	fun testErrorReturnFromLocalDataSource() = runBlocking {
		Assert.assertTrue(repository.loadFuelHistory(invalidLimit) is ResultState.Failure)
	}
	
}