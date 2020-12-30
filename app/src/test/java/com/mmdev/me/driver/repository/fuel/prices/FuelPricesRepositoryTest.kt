/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel.prices

import com.mmdev.me.driver.data.datasource.fuel.prices.api.IFuelPricesApiDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.IFuelPricesLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationAndPrices
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.data.repository.fuel.prices.FuelPricesRepositoryImpl
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.FuelType.*
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.fuel.prices.data.Region.KYIV
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
	private val apiDataSource: IFuelPricesApiDataSource = mockk()
	private val mappers = FuelPriceMappersFacade()
	
	private val repository: IFuelPricesRepository =
		FuelPricesRepositoryImpl(localDataSource, apiDataSource, mappers)
	
	private val validDbReturn = listOf(
		FuelStationAndPrices(
			FuelStationEntity("WOG", "wog", "01-01-2020", KYIV.id),
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
			localDataSource.getFuelStationsAndPrices(validDate, KYIV.id)
		} returns ResultState.Success(validDbReturn)
		coEvery {
			apiDataSource.requestFuelPrices(validDate, KYIV)
		} returns ResultState.Success(validNetworkReturn)
		
		
		//invalid date
		//cache and remote data are not exists
		coEvery {
			localDataSource.getFuelStationsAndPrices(invalidDate, KYIV.id)
		} returns ResultState.Failure(Exception("Invalid date"))
		coEvery {
			apiDataSource.requestFuelPrices(invalidDate, KYIV)
		} returns ResultState.Failure(Exception("Invalid date"))
		
		
		//valid date
		//cache IS EMPTY but remote data exists
		coEvery {
			localDataSource.getFuelStationsAndPrices(emptyCacheDate, KYIV.id)
		} returns ResultState.Failure(Exception("Empty Cache"))
		
		coEvery {
			apiDataSource.requestFuelPrices(emptyCacheDate, KYIV)
		} returns ResultState.Success(validNetworkReturn)
		
	}
	
	/**
	 * Scenario:
	 * [IFuelPricesLocalDataSource] has data for given date -> return data to user
	 * [IFuelPricesApiDataSource] won't be called
	 */
	@Test
	fun testSuccessfulReturnFromLocalDataSource() = runBlocking {
		
		val result = repository.getFuelStationsWithPrices(validDate, KYIV)
		
		coVerify {
			localDataSource.getFuelStationsAndPrices(validDate, KYIV.id)
		}
		
		coVerify { apiDataSource.requestFuelPrices(validDate, KYIV) wasNot Called }
		
		assertTrue(result is ResultState.Success)
		
		result.fold(
			success = { assertTrue(it.isNotEmpty()) },
			failure = { assertTrue(it.message == "Invalid date")}
		)
		
	}
	
	/**
	 * Scenario:
	 * bad date format given to [IFuelPricesApiDataSource] and [IFuelPricesLocalDataSource]
	 * throw an error
	 */
	@Test
	fun testFailureReturnOnInvalidDate() = runBlocking {
		val result = repository.getFuelStationsWithPrices(invalidDate, KYIV)
		
		coVerifyOrder {
			localDataSource.getFuelStationsAndPrices(invalidDate, KYIV.id)
			apiDataSource.requestFuelPrices(invalidDate, KYIV)
		}
		
		assertTrue(result is ResultState.Failure)
		
		result.fold(
			success = { assertTrue(it.isNullOrEmpty()) },
			failure = { assertTrue(it.message == "Invalid date") }
		)
		
	}
	
}