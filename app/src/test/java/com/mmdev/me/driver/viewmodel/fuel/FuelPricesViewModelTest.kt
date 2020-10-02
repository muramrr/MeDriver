/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.10.2020 16:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.viewmodel.fuel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mmdev.me.driver.core.utils.currentTimeAndDate
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.fuel.prices.IFuelPricesRepository
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class FuelPricesViewModelTest {
	
	private val repository: IFuelPricesRepository = mockk()
	private lateinit var viewModel: FuelPricesViewModel
	private val fuelPricesStateObserver = mockk<Observer<FuelPricesViewState>>(relaxed = true)
	
	
	private val loadingState = FuelPricesViewState.Loading
	private val successState = FuelPricesViewState.Success(listOf(FuelStationWithPrices()))
	
	private val validDate = currentTimeAndDate().date.toString()
	
	@get:Rule
	val rule = InstantTaskExecutorRule()
	
	private val testDispatcher = TestCoroutineDispatcher()
	
	@Before
	fun setUp() {
		Dispatchers.setMain(testDispatcher)
		coEvery {
			repository.getFuelStationsWithPrices(validDate)
		} returns ResultState.Success(listOf(FuelStationWithPrices()))
		
		viewModel = FuelPricesViewModel(repository)
	}
	
	@After
	fun after() {
		Dispatchers.resetMain()
		testDispatcher.cleanupTestCoroutines()
	}

	@Test
	fun getFuelPrices() = runBlocking {
		viewModel.fuelPricesState.observeForever(fuelPricesStateObserver)
		viewModel.getFuelPrices()
		coVerify { repository.getFuelStationsWithPrices(validDate) }

		delay(10)
		
		coVerifyOrder {
			//fuelPricesStateObserver.onChanged(loadingState)
			fuelPricesStateObserver.onChanged(successState)
		}
		
		assertEquals(viewModel.fuelPrices.value, listOf(FuelStationWithPrices()))
	}
}