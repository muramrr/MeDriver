/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.repository.fuel.history

import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelPriceEmbedded
import com.mmdev.me.driver.data.datasource.fuel.history.server.IFuelHistoryServerDataSource
import com.mmdev.me.driver.data.repository.fuel.history.FuelHistoryRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.FuelType.A95
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
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
	private val serverDataSource: IFuelHistoryServerDataSource = mockk()
	private val mappers = FuelHistoryMappersFacade()
	
	private val repository: IFuelHistoryRepository = FuelHistoryRepositoryImpl(
		localDataSource,
		serverDataSource,
		mappers
	)
	
	private val validLimit = 20
	private val validOffset = 0
	private val invalidLimit = 1
	
	private val time = currentEpochTime()
	
	private val validReturn = listOf (
		FuelHistoryEntity(
			commentary = "",
			date = currentEpochTime(),
			dateAdded = time,
			distancePassedBound = DistanceBound(kilometers = 400, miles = null),
			filledLiters = 0.0,
			fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
			fuelPrice = FuelPriceEmbedded(15.0, A95.name),
			fuelStation = FuelStation("OKKO", "okko", "01-01-2020"),
			moneySpent = 1651.0,
			odometerValueBound = DistanceBound(kilometers = 2000, miles = null),
			vehicleVinCode = "someVin",
		)
	)
	
	@Before
	fun before() {
		coEvery {
			localDataSource.getFuelHistory("someVin", validLimit, validOffset)
		} returns ResultState.Success(validReturn)
		
		coEvery {
			localDataSource.getFuelHistory("someVin", invalidLimit, validOffset)
		} returns ResultState.Failure(Exception("Invalid"))
	}
	
	@Test
	fun testSuccessfulReturnFromLocalDataSource() = runBlocking {
		
		val result = repository.getInitFuelHistory("someVin")
	
		result.fold(
			success = { Assert.assertTrue(it.isNotEmpty()) },
			failure = { }
		)
		
		Assert.assertTrue(result is ResultState.Success)
		
	}
	
	@Test
	fun testErrorReturnFromLocalDataSource() = runBlocking {
		Assert.assertTrue(repository.getInitFuelHistory("someVin") is ResultState.Failure)
	}
	
}