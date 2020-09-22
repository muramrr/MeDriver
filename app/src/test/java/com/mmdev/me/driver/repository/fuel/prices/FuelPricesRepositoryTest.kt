/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 17:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel.prices

import com.mmdev.me.driver.data.datasource.fuel.prices.local.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.IFuelPricesRemoteDataSource
import com.mmdev.me.driver.data.repository.fuel.prices.FuelPricesRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.FuelType.A100
import com.mmdev.me.driver.domain.fuel.FuelType.A95
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.repository.fuel.FuelConstants
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Tests for [IFuelPricesRepository]
 */

@RunWith(JUnit4::class)
class FuelPricesRepositoryTest {
	
	private val localDataSource: IFuelPricesLocalDataSource = mockk(relaxed = true)
	private val remoteDataSource: IFuelPricesRemoteDataSource = mockk()
	private val mappers = FuelPriceMappersFacade()
	
	private val repository: IFuelPricesRepository =
		FuelPricesRepositoryImpl(localDataSource, remoteDataSource, mappers)
	
	private val validDbReturn = listOf(
		FuelStationAndPrices(
			FuelStationEntity("WOG", "wog", "01-01-2020"),
			listOf(
				FuelPriceEntity("wog", 21.0, A100.code),
				FuelPriceEntity("wog", 14.0, A95.code)
			)
		)
	)
	
	private val validNetworkReturn = FuelConstants.dtoResponse
	
	
	private val validDate = "10-01-2020"
	private val emptyCacheDate = "10-02-2020"
	private val invalidDate = "01.01.2020"
	
	@Before
	fun before() {
		//valid date
		//cache exists and remote data exists
		coEvery {
			localDataSource.getFuelStationsAndPrices(validDate)
		} returns ResultState.Success(validDbReturn)
		coEvery {
			remoteDataSource.requestFuelPrices(validDate)
		} returns ResultState.Success(validNetworkReturn)
		
		
		//invalid date
		//cache and remote data are not exists
		coEvery {
			localDataSource.getFuelStationsAndPrices(invalidDate)
		} returns ResultState.Failure(Exception("Invalid date"))
		coEvery {
			remoteDataSource.requestFuelPrices(invalidDate)
		} returns ResultState.Failure(Exception("Invalid date"))
		
		
		//valid date
		//cache IS EMPTY but remote data exists
		coEvery {
			localDataSource.getFuelStationsAndPrices(emptyCacheDate)
		} returns ResultState.Failure(Exception("Empty Cache"))
		
		coEvery {
			remoteDataSource.requestFuelPrices(emptyCacheDate)
		} returns ResultState.Success(validNetworkReturn)
		
	}
	
	/**
	 * Scenario:
	 * [IFuelPricesLocalDataSource] has data for given date -> return data to user
	 * [IFuelPricesRemoteDataSource] won't be called
	 */
	@Test
	fun testSuccessfulReturnFromLocalDataSource() = runBlocking {
		
		val result = repository.getFuelStationsWithPrices(validDate)
		
		coVerify {
			localDataSource.getFuelStationsAndPrices(validDate)
		}
		
		coVerify { remoteDataSource.requestFuelPrices(validDate) wasNot Called }
		
		assertTrue(result is ResultState.Success)
		
		result.fold(
			success = { assertTrue(it.isNotEmpty()) },
			failure = { assertTrue(it.message == "Invalid date")}
		)
		
	}
	
	/**
	 * Scenario:
	 * bad date format given to [IFuelPricesRemoteDataSource] and [IFuelPricesLocalDataSource]
	 * throw an error
	 */
	@Test
	fun testFailureReturnOnInvalidDate() = runBlocking {
		val result = repository.getFuelStationsWithPrices(invalidDate)
		
		coVerifyOrder {
			localDataSource.getFuelStationsAndPrices(invalidDate)
			remoteDataSource.requestFuelPrices(invalidDate)
		}
		
		assertTrue(result is ResultState.Failure)
		
		result.fold(
			success = { assertTrue(it.isNullOrEmpty()) },
			failure = { assertTrue(it.message == "Invalid date") }
		)
		
	}
	
}