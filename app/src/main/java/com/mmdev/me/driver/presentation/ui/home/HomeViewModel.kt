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

package com.mmdev.me.driver.presentation.ui.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.domain.home.IHomeRepository
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.vehicle.VehicleConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayAt

/**
 *
 */

class HomeViewModel(private val repository: IHomeRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<HomeViewState> = MutableLiveData()
	
	val vehicles: MutableLiveData<List<Pair<Vehicle, Expenses>>> = MutableLiveData()
	val isVehicleListEmpty: MutableLiveData<Boolean> = MutableLiveData()
	
	val expensesPerYear: MutableLiveData<List<Expenses>> = MutableLiveData()
	
	init {
		getMyGarage()
		getExpensesPerYear()
	}
	
	fun getMyGarage() {
		viewModelScope.launch {
			repository.getGarage().fold(
				success = {
					vehicles.postValue(it)
					
					isVehicleListEmpty.value = it.isEmpty()
					
				},
				failure = {
					viewState.postValue(HomeViewState.Error(it.localizedMessage))
				}
			)
		}
	}
	
	fun getExpensesPerYear() {
		viewModelScope.launch {
			expensesPerYear.postValue(repository.getExpensesByTimeRange(generateMonthsRange()))
		}
	}
	
	private fun generateMonthsRange(): List<Pair<Long, Long>> {
		val year = Clock.System.todayAt(TimeZone.currentSystemDefault()).year
		return (1..12).map {
			Pair(
				LocalDate(year, it, 1)
					.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
				
				LocalDate(year, it, when (it) {
					in arrayOf(1, 3, 5, 7, 8, 10, 12) -> 31
					2 -> if (DateHelper.isYearLeap(year)) 29 else 28
					else -> 30
				}
				).atStartOfDayIn(TimeZone.currentSystemDefault())
					//add a day duration to match all day range eg: jan 01 00:00 - 31 23:59:59.999
					.toEpochMilliseconds() + DateHelper.DAY_DURATION - 1
			)
		}
	}
	
	//todo: delete
	fun generateRandomData(context: Context) {
	
		viewState.postValue(HomeViewState.GeneratingStarted)
		VehicleConstants.vehicleBrands.shuffled().take(10).forEach {
			viewModelScope.launch {
				delay(100)
				DataGenerator.generateVehicle(context, it)
			}
		}
		viewState.postValue(HomeViewState.GenerationCompleted)
		MedriverApp.dataGenerated = true
		
	}
	
}