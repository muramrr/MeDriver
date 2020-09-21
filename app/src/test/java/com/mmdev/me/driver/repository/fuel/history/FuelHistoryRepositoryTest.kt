/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel.history

import com.mmdev.me.driver.core.utils.DateConverter
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.VehicleWithFuelHistory
import com.mmdev.me.driver.data.datasource.fuel.history.remote.IFuelHistoryRemoteDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.repository.fuel.history.FuelHistoryRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.FuelType.A95
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.model.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound
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
	private val remoteDataSource: IFuelHistoryRemoteDataSource = mockk()
	private val mappers = FuelHistoryMappersFacade()
	
	private val repository: IFuelHistoryRepository = FuelHistoryRepositoryImpl(
		localDataSource,
		remoteDataSource,
		mappers
	)
	
	private val validLimit = 20
	private val validOffset = 0
	private val invalidLimit = 1
	
	private val vehicleEntity = VehicleEntity(
		"Ford",
		"Focus",
		2013,
		"vin",
		DistanceBound(kilometers = 2000, miles = null)
	)
	private val validReturn = VehicleWithFuelHistory(
		vehicleEntity,
		listOf (
			FuelHistoryEntity(
				commentary = "",
				distancePassedBound = DistanceBound(kilometers = 400, miles = null),
				filledLiters = 0.0,
				fuelConsumptionBound = ConsumptionBound(consumptionKM = 0.0, consumptionMI = null),
				fuelPrice = FuelPriceEntity("okko", 15.0, A95.code),
				fuelStation = FuelStationEntity("OKKO", "okko", "01-01-2020"),
				odometerValueBound = DistanceBound(kilometers = 2000, miles = null),
				vehicleVinCode = "someVin",
				timestamp = DateConverter.toDate("12-01-2020").time
			)
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
		
		val result = repository.loadFuelHistory("someVin", null)
	
		result.fold(
			success = { Assert.assertTrue(it.isNotEmpty()) },
			failure = { }
		)
		
		Assert.assertTrue(result is ResultState.Success)
		
	}
	
	@Test
	fun testErrorReturnFromLocalDataSource() = runBlocking {
		Assert.assertTrue(repository.loadFuelHistory("someVin", invalidLimit) is ResultState.Failure)
	}
	
}