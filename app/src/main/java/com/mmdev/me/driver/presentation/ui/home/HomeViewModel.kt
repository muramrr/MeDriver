/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.12.2020 14:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.home.IHomeRepository
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.fuel.FuelStationConstants
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants
import com.mmdev.me.driver.presentation.ui.vehicle.VehicleConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
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

class HomeViewModel(
	private val repository: IHomeRepository,
	private val vehicleRepo: IVehicleRepository,
	private val maintenanceRepo: IMaintenanceRepository,
	private val fuelHistoryRepo: IFuelHistoryRepository
): BaseViewModel() {
	
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
	
	//todo: delete
	fun generateRandomData(context: Context) {
		viewModelScope.launch(MyDispatchers.io()) {
			viewState.postValue(HomeViewState.GeneratingStarted)
			VehicleConstants.vehicleBrands.forEach {
				generateVehicles(context, it)
			}
			viewState.postValue(HomeViewState.GenerationCompleted)
			MedriverApp.dataGenerated = true
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
					.toEpochMilliseconds() + 86400000 - 1
			)
		}
	}
	
	private suspend fun generateVehicles(context: Context, brand: String) {
		with(
			Vehicle(
				brand,
				generateRandomString(),
				Random.nextInt(2000, 2020),
				generateRandomVinCode(),
				DistanceBound(kilometers = Random.nextInt(1000, 200000), miles = null),
				Random.nextDouble(1.0, 8.0).roundTo(1),
				generateRandomDate()
			)
		) {
			vehicleRepo.addVehicle(MedriverApp.currentUser, this).collect { result ->
				result.fold(
					success = {
						//logWtf(TAG, "generated vehicle with vin = ${this.vin}")
					},
					failure = {
						logError(TAG, "${it.message}")
					}
				)
			}
			generateMaintenanceData(context, this.vin)
			generateFuelHistoryData(this.vin)
		}
		
	}
	
	private suspend fun generateMaintenanceData(context: Context, vin: String) {
		VehicleSystemNodeType.valuesArray.forEach { parent ->
			maintenanceRepo.addMaintenanceItems(
				MedriverApp.currentUser,
				parent.getChildren().map { child ->
					delay(2)
					VehicleSparePart(
						commentary = generateRandomString(5, 20),
						date = convertToLocalDateTime(Random.nextLong(1577829600000, 1609451999000)),
						dateAdded = currentEpochTime(),
						articulus = generateRandomString(2, 6),
						vendor = generateRandomString(4, 8),
						systemNode = parent,
						systemNodeComponent = child,
						searchCriteria = if (child.getSparePartName() != SparePart.OTHER)
							LocaleHelper.getStringFromAllLocales(context, VehicleSystemNodeConstants
								.childrenMap[parent]!![child.getSparePartOrdinal()]
							).map { it.value }
						else listOf(child.getSparePartName()),
						moneySpent = Random.nextInt(100, 9999).toDouble(),
						odometerValueBound = DistanceBound(
							kilometers = Random.nextInt(100, 200000),
							miles = null
						),
						vehicleVinCode = vin
					)
				}
			).collect { result ->
				result.fold(
					success = {
						//logWtf(TAG, "generated for $parent")
					},
					failure = {
						logError(TAG, "${it.message}")
					}
				)
			}
			
		}
	}
	
	private suspend fun generateFuelHistoryData(vin: String) {
		fuelHistoryRepo.importFuelHistory(
			null,
			FuelStationConstants.fuelStationList.map {
				delay(2)
				FuelHistory(
					commentary = generateRandomString(5, 10),
					date = convertToLocalDateTime(Random.nextLong(1577829600000, 1609451999000)),
					dateAdded = currentEpochTime(),
					distancePassedBound = DistanceBound(
						kilometers = Random.nextInt(100, 500),
						miles = null
					),
					filledLiters = Random.nextDouble(1.0, 100.0).roundTo(1),
					fuelConsumptionBound = ConsumptionBound(
						consumptionKM = Random.nextDouble(15.0, 31.0).roundTo(1),
						consumptionMI = null
					),
					fuelPrice = FuelPrice(
						Random.nextDouble(15.0, 31.0).roundTo(2),
						FuelType.values()[Random.nextInt(0, FuelType.values().size -1)]
					),
					fuelStation = it,
					odometerValueBound = DistanceBound(
						kilometers = Random.nextInt(100, 200000),
						miles = null
					),
					vehicleVinCode = vin
				)
			}
			
		).collect { result ->
			result.fold(
				success = {
					//logWtf(TAG, "generated fuel history for station $it")
				},
				failure = {
					logError(TAG, "${it.message}")
				}
			)
		}
		
		
	}
	
	private fun generateRandomString(min: Int = 5, max: Int = 10): String {
		val allowedChars = ('A'..'Z') + ('a'..'z')
		return (min..max)
			.map { allowedChars.random() }
			.joinToString("")
	}
	
	private fun generateRandomVinCode(): String {
		
		val allowedChars = "ABCDEFGHJKLMNPRSTUVWXYZ1234567890"
		return (1..17)
			.map { allowedChars.random() }
			.joinToString("")
		
	}
	
	private fun generateRandomDate(): String {
		return "${Random.nextInt(10, 30)}.0${Random.nextInt(3, 9)}.2020"
	}
	
}