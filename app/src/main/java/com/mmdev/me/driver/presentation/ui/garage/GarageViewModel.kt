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

package com.mmdev.me.driver.presentation.ui.garage

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.domain.garage.IGarageRepository
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.vehicle.data.VehicleConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayAt
import kotlin.random.Random

/**
 *
 */

class GarageViewModel(private val repository: IGarageRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<GarageViewState> = MutableLiveData()
	
	val vehicles = MutableLiveData<List<Vehicle>>()
	val vehiclesWithExpenses = MutableLiveData<List<Pair<Vehicle, Expenses>>>()
	val isVehicleListEmpty = MutableLiveData<Boolean>()
	
	val expensesPerYear = MutableLiveData<List<Expenses>>()
	
	init {
		getMyGarage()
		getExpensesPerYear()
	}
	
	fun getMyGarage() {
		viewModelScope.launch {
			val result = repository.getGarage()
			isVehicleListEmpty.value = result.isEmpty()
			
			vehicles.postValue(result.map { it.first })
			if (result.any { it.second.maintenance != 0.0 || it.second.fuel != 0.0 })
				vehiclesWithExpenses.postValue(result)
		}
	}
	
	fun getExpensesPerYear() {
		viewModelScope.launch {
			val result = repository.getExpensesByTimeRange(generateMonthsRange())
			if (result.any { it.fuel != 0.0 || it.maintenance != 0.0 }) expensesPerYear.postValue(result)
		}
	}
	
	private fun generateMonthsRange(): List<Pair<Long, Long>> {
		//get current year
		val year = Clock.System.todayAt(TimeZone.currentSystemDefault()).year
		//for each month calculate period in epoch millis
		return (1..12).map { month ->
			Pair( //first day in month time epoch
				LocalDate(year = year, monthNumber = month, dayOfMonth = 1)
					.atStartOfDayIn(TimeZone.currentSystemDefault())
					.toEpochMilliseconds(),
				
				//last day in month time epoch
				LocalDate(
					year = year,
					monthNumber = month,
					dayOfMonth = when (month) { /** define number of days in month */
						in arrayOf(1, 3, 5, 7, 8, 10, 12) -> 31 /** months with 31 days */
						 2 -> if (DateHelper.isYearLeap(year)) 29 else 28 /** february */
						else -> 30 /** months with 30 days */
					}
				).atStartOfDayIn(TimeZone.currentSystemDefault())
					/** add a day duration to match all day range eg: jan 01 00:00 - 31 23:59:59.999*/
					.toEpochMilliseconds() + DateHelper.DAY_DURATION - 1
			)
		}
	}
	
	//note: data generator
	fun generateRandomData(context: Context) {
		viewModelScope.launch {
			viewState.postValue(GarageViewState.GeneratingStarted)
			VehicleConstants.vehicleBrands.shuffled().take(Random.nextInt(2, 5)).forEach {
					delay(100)
					DataGenerator.generateVehicle(context, it)
				}
			viewState.postValue(GarageViewState.GenerationCompleted)
			MedriverApp.dataGenerated = true
		}
	}
	
}