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
import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.fuel.FuelStationConstants
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

/**
 * todo: delete
 */

object DataGenerator: KoinComponent {
	
	private val vehicleRepo: IVehicleRepository by inject()
	private val maintenanceRepo: IMaintenanceRepository by inject()
	private val fuelHistoryRepo: IFuelHistoryRepository by inject()
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	suspend fun generateVehicle(context: Context, brand: String) {
		with(
			Vehicle(
				brand,
				generateRandomString(),
				Random.nextInt(2000, 2020),
				generateRandomVinCode(),
				DistanceBound(kilometers = Random.nextInt(1000, 200000), miles = null),
				Random.nextDouble(1.0, 8.0).roundTo(1),
				generateRandomDate(),
				currentEpochTime()
			)
		) {
			vehicleRepo.addVehicle(MainActivity.currentUser, this).collect { result ->
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
	
	private suspend fun generateMaintenanceData(context: Context, vin: String) =
		VehicleSystemNodeType.valuesArray.forEach { parent ->
			delay(100)
			maintenanceRepo.addMaintenanceItems(
				MainActivity.currentUser,
				parent.getChildren().toList().shuffled().take(2).map { child ->
					delay(500)
					VehicleSparePart(
						commentary = generateRandomString(5, 20),
						date = convertToLocalDateTime(
							Random.nextLong(1577829600000, 1609451999000)),
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
	
	private suspend fun generateFuelHistoryData(vin: String) = FuelStationConstants
		.fuelStationList
		.shuffled()
		.take(5)
		.forEach {
			delay(500)
			fuelHistoryRepo.addFuelHistoryRecord(
				MainActivity.currentUser,
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
							FuelType.values()[Random.nextInt(0, FuelType.values().size - 1)]
						),
						fuelStation = it,
						odometerValueBound = DistanceBound(
							kilometers = Random.nextInt(100, 200000),
							miles = null
						),
						vehicleVinCode = vin
					)
			
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