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
import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.core.utils.extensions.toEpochTime
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.data.datasource.fuel.history.local.IFuelHistoryLocalDataSource
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelPriceEmbedded
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.MaintenanceEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.IVehicleLocalDataSource
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.MaintenanceRegulationsEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.RegulationEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
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
import com.mmdev.me.driver.presentation.ui.vehicle.data.VehicleConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

/**
 * note: data generator
 */

object DataGenerator: KoinComponent {
	
	private val vehicleRepo: IVehicleRepository by inject()
	private val maintenanceRepo: IMaintenanceRepository by inject()
	private val fuelHistoryRepo: IFuelHistoryRepository by inject()
	
	private val vehicleLocal: IVehicleLocalDataSource by inject()
	private val maintenanceLocal: IMaintenanceLocalDataSource by inject()
	private val fuelHistoryLocal: IFuelHistoryLocalDataSource by inject()
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	private val randomVendors = arrayListOf<String>(
		"Excel-G",
		"Bosch",
		"Bosch",
		"Denso",
		"Magna",
		"Continental",
		"ZF",
		"BPW",
		"FRAP",
		"DIESEL TECHNIC",
		"AUGER",
		"SIDEM",
		"LEMFORDER",
		"Aisin",
		"Seiki",
		"Hyundai",
		"Mobis",
		"Lear",
		"Faurecia",
		"Valeo",
		"SACHS",
	)
	
	suspend fun generateVehicle(context: Context, brand: String) {
		with(
			Vehicle(
				brand,
				"model",
				Random.nextInt(2000, 2020),
				generateRandomVinCode(),
				generateDistanceBound(100, 200000),
				Random.nextDouble(1.0, 5.0).roundTo(1),
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
			generateFuelHistoryData(this.vin, 15)
		}
		
	}
	
	private suspend fun generateMaintenanceData(context: Context, vin: String) =
		VehicleSystemNodeType.valuesArray.forEach { parent ->
			delay(100)
			maintenanceRepo.addMaintenanceItems(
				MainActivity.currentUser,
				parent.getChildren().toList().shuffled().take(5).map { child ->
					VehicleSparePart(
						commentary = "Random commentary looks like this",
						date = convertToLocalDateTime(
							Random.nextLong(1577829600000, 1609451999000)),
						dateAdded = currentEpochTime() + Random.nextLong(0, 1609451999000),
						articulus = generateRandomArticulus(2, 6),
						vendor = randomVendors.random(),
						systemNode = parent,
						systemNodeComponent = child,
						searchCriteria = if (child.getSparePartName() != SparePart.OTHER)
							LocaleHelper.getStringFromAllLocales(context, VehicleSystemNodeConstants
								.childrenMap[parent]!![child.getSparePartOrdinal()]
							).map { it.value }
						else listOf(child.getSparePartName()),
						moneySpent = Random.nextInt(100, 9999).toDouble(),
						odometerValueBound = generateDistanceBound(100, 200000),
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
	
	private suspend fun generateFuelHistoryData(vin: String, howMany: Int? = null) = FuelStationConstants
		.fuelStationList
		.shuffled()
		.take(howMany ?: FuelStationConstants.fuelStationList.size)
		.forEach {
			delay(200)
			fuelHistoryRepo.addFuelHistoryRecord(
				MainActivity.currentUser,
					FuelHistory(
						commentary = "Random commentary looks like this",
						date = convertToLocalDateTime(Random.nextLong(1577829600000, 1609451999000)),
						dateAdded = currentEpochTime(),
						distancePassedBound = generateDistanceBound(100, 500),
						filledLiters = Random.nextDouble(1.0, 100.0).roundTo(1),
						fuelConsumptionBound = ConsumptionBound(
							consumptionKM = Random.nextDouble(15.0, 31.0).roundTo(1),
							consumptionMI = null
						),
						fuelPrice = FuelPrice(
							Random.nextDouble(15.0, 31.0).roundTo(2),
							FuelType.values().random()
						),
						fuelStation = it,
						odometerValueBound = generateDistanceBound(100, 200000),
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
		
	
	
	private fun generateRandomArticulus(min: Int = 5, max: Int = 10): String {
		val allowedChars = ('A'..'Z') + ('0'..'9') + '-'
		return allowedChars.shuffled().take(Random.nextInt(min, max)).joinToString("")
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
	
	private fun generateDistanceBound(start: Int, end: Int) = DistanceBound(
		kilometers = Random.nextInt(start, end),
		miles = null
	)
	
	suspend fun fastGenerateFuelHistory(vin: String) = FuelStationConstants
		.fuelStationList
		.shuffled()
		.forEach { station ->
			delay(500)
			fuelHistoryLocal.importFuelHistory(
				FuelType.values().map {
					FuelHistoryEntity(
						commentary = "Random commentary looks like this",
						date = convertToLocalDateTime(
							Random.nextLong(1577829600000, 1609451999000)
						).toEpochTime(),
						dateAdded = currentEpochTime() + Random.nextLong(0, 1609451999000),
						distancePassedBound = generateDistanceBound(100, 500),
						filledLiters = Random.nextDouble(1.0, 100.0).roundTo(1),
						fuelConsumptionBound = ConsumptionBound(
							consumptionKM = Random.nextDouble(15.0, 31.0).roundTo(1),
							consumptionMI = null
						),
						fuelPrice = FuelPriceEmbedded(
							Random.nextDouble(15.0, 31.0).roundTo(2),
							it.toString()
						),
						fuelStation = station,
						moneySpent = Random.nextDouble(2000.0, 5000.0),
						odometerValueBound = generateDistanceBound(100, 200000),
						vehicleVinCode = vin
					)
				}
			).fold(
				success = {
					logWtf(TAG, "Fuel history generated and imported")
				},
				failure = {
				
				}
			)
		}
	
	suspend fun fastGenerateMaintenance(context: Context, vin: String) =
		VehicleSystemNodeType.valuesArray.forEach{ parent ->
			delay(100)
			maintenanceLocal.importReplacedSpareParts(
				parent.getChildren().toList().shuffled().map { child ->
					MaintenanceEntity(
						commentary = "Random commentary looks like this",
						date = convertToLocalDateTime(
							Random.nextLong(1577829600000, 1609451999000)
						).toEpochTime(),
						dateAdded = currentEpochTime() + Random.nextLong(0, 1609451999000),
						articulus = generateRandomArticulus(2, 6),
						vendor = randomVendors.random(),
						systemNode = parent.toString(),
						systemNodeComponent = child.getSparePartName(),
						searchCriteria = if (child.getSparePartName() != SparePart.OTHER)
							LocaleHelper.getStringFromAllLocales(context, VehicleSystemNodeConstants
								.childrenMap[parent]!![child.getSparePartOrdinal()]
							).map { it.value }.joinToString()
						else listOf(child.getSparePartName()).joinToString(),
						moneySpent = Random.nextInt(100, 9999).toDouble(),
						odometerValueBound = generateDistanceBound(100, 200000),
						vehicleVinCode = vin
					)
				}
			).fold(
				success = {
					logWtf(TAG, "Maintenance generated and imported")
				},
				failure = {
					logError(TAG, "${it.message}")
				}
			)
			
			
		}

	suspend fun fastGenerateVehicles() = vehicleLocal.importVehicles(
		VehicleConstants.vehicleBrands
			.shuffled()
			.take(Random.nextInt(2, 5))
			.map {
				VehicleEntity(
					brand = it,
					model = "model",
					year = Random.nextInt(2000, 2020),
					vin = generateRandomVinCode(),
					odometerValueBound = generateDistanceBound(100, 200000),
					engineCapacity = Random.nextDouble(1.0, 5.0).roundTo(1),
					lastRefillDate = generateRandomDate(),
					lastUpdatedDate = currentEpochTime() + Random.nextLong(0, 1000),
					maintenanceRegulations = MaintenanceRegulationsEntity(
						insurance = randomRegulationEntity(),
						airFilter = randomRegulationEntity(),
						engineFilterOil = randomRegulationEntity(),
						fuelFilter = randomRegulationEntity(),
						cabinFilter = randomRegulationEntity(),
						brakesFluid = randomRegulationEntity(),
						grmKit = randomRegulationEntity(),
						plugs = randomRegulationEntity(),
						transmissionFilterOil = randomRegulationEntity()
					)
				)
			}
	).fold(
		success = {
			logWtf(TAG, "Vehicles generated and imported")
		},
		failure = {
			logError(TAG, "${it.message}")
		}
	)
	
	private fun randomRegulationEntity() = RegulationEntity(
		generateDistanceBound(10000, 100000),
		DateHelper.YEAR_DURATION * Random.nextInt(1, 10)
	)
	
	
}