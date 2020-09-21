/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 20:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.viewmodel.fuel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewModel
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
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
class FuelHistoryViewModelTest {
	
	private val vehicleDomain = Vehicle(
		"Ford",
		"Focus",
		2013,
		"vin",
		DistanceBound(kilometers = 2000, miles = null)
	)
	
	private val repository: IFuelHistoryRepository = mockk(relaxed = true)
	private val repoSuccessStateGet: SimpleResult<List<FuelHistoryRecord>> =
		ResultState.Success(listOf(FuelHistoryRecord(vehicle = vehicleDomain)))
	private val repoFailureStateGet = ResultState.Failure(Exception("Error"))
	private val repoSuccessStateUnit: SimpleResult<Unit> = ResultState.Success(Unit)
	
	
	private lateinit var viewModel: FuelHistoryViewModel
	private val historyStateObserver = mockk<Observer<FuelHistoryViewState>>(relaxed = true)
	
	private val fuelHistoryRecord = FuelHistoryRecord(filledLiters = 1.0, vehicle = vehicleDomain)
	
	
	private val loadingState = FuelHistoryViewState.Loading
	private val initState = FuelHistoryViewState.Init(listOf(FuelHistoryRecord(vehicle = vehicleDomain)))
	private val paginateState = FuelHistoryViewState.Paginate(listOf(FuelHistoryRecord(vehicle = vehicleDomain)))
	private val insertNewOneState = FuelHistoryViewState.InsertNewOne(fuelHistoryRecord)
	
	
	@get:Rule
	val rule = InstantTaskExecutorRule()
	
	private val testDispatcher = TestCoroutineDispatcher()
	
	@Before
	fun before() {
		Dispatchers.setMain(testDispatcher)
		coEvery { repository.loadFuelHistory("vin", null) } returns repoSuccessStateGet
		coEvery { repository.loadFuelHistory("vin", 10) } returns repoSuccessStateGet
		coEvery { repository.loadFuelHistory("vin", -1) } returns repoFailureStateGet
		
		coEvery { repository.addFuelHistoryRecord(fuelHistoryRecord) } returns repoSuccessStateUnit
		
		viewModel = FuelHistoryViewModel(repository)
	}
	
	@After
	fun after() {
		Dispatchers.resetMain()
		testDispatcher.cleanupTestCoroutines()
	}

	@Test
	fun findFuelStationBySlug() {
	
	}

	@Test
	fun clearInputFields() {
	
	}

	@Test
	fun addHistoryRecord() = runBlocking {
		viewModel.fuelHistoryState.observeForever(historyStateObserver)
		viewModel.addHistoryRecord(fuelHistoryRecord)
		coVerify { repository.addFuelHistoryRecord(fuelHistoryRecord) }
		coVerify {
			historyStateObserver.onChanged(insertNewOneState)
		}
	}
	

	@Test
	fun getHistoryRecords() = runBlocking {
		viewModel.fuelHistoryState.observeForever(historyStateObserver)
		
		//init
		viewModel.getHistoryRecords(null)
		coVerify { repository.loadFuelHistory("vin", null) }
		delay(10)
		coVerifyOrder {
			historyStateObserver.onChanged(loadingState)
			historyStateObserver.onChanged(initState)
		}
		
		//paginate
		viewModel.getHistoryRecords(10)
		coVerify { repository.loadFuelHistory("vin", 10) }
		delay(10)
		coVerifyOrder {
			historyStateObserver.onChanged(loadingState)
			historyStateObserver.onChanged(paginateState)
		}
		
		
		
	}
}