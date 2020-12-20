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

package com.mmdev.me.driver.data.datasource.vehicle.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.MaintenanceEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.vehicle.data.Expenses

/**
 * Dao responsible for storing and retrieving vehicles info from database
 */

@Dao
interface VehicleDao {
	
	@Query("""
		SELECT consumptionPer100MI, consumptionPer100KM FROM
		${MeDriverRoomDatabase.FUEL_HISTORY_TABLE} WHERE vehicleVinCode = :vin
	""")
	suspend fun getConsumption(vin: String): List<ConsumptionBound>
	
	@Transaction
	suspend fun getExpenses(vin: String): Expenses {
		return Expenses(
			getExpensesMaintenance(vin) ?: 0.0,
			getExpensesFuelHistory(vin) ?: 0.0
		)
	}
	
	@Query(
		"""
		SELECT SUM(moneySpent) as fuelExpenses
		FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		"""
	)
	suspend fun getExpensesFuelHistory(vin: String): Double?
	
	@Query(
		"""
		SELECT SUM(moneySpent)
		FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		"""
	)
	suspend fun getExpensesMaintenance(vin: String): Double?
	
	@Transaction
	suspend fun getPlannedReplacements(vin: String): Map<String, MaintenanceEntity> {
		return PlannedParts.valuesArray.map { it.getSparePartName() }.zip(
			PlannedParts.valuesArray.map {
				getPlannedLastReplacement(vin, it.getSparePartName())
			}
		).toMap()
	}
	
	@Query(
		"""
		SELECT * FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode =:vin AND systemNodeComponent = :plannedSparePart
		ORDER BY date DESC
		LIMIT 1
		"""
	)
	suspend fun getPlannedLastReplacement(vin: String, plannedSparePart: String): MaintenanceEntity
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.VEHICLES_TABLE}")
	suspend fun getAllVehicles(): List<VehicleEntity>
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.VEHICLES_TABLE} WHERE vin = :vin")
	suspend fun getVehicleByVin(vin: String): VehicleEntity?
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertVehicle(vehicle: VehicleEntity)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun importVehicles(vehiclesToImport: List<VehicleEntity>)
	
	//cascade deleting
	@Query("DELETE FROM ${MeDriverRoomDatabase.VEHICLES_TABLE} WHERE vin = :vin")
	suspend fun deleteVehicle(vin: String)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.VEHICLES_TABLE} WHERE vin = :vin")
	suspend fun deleteVehicleByVin(vin: String)
	@Query("DELETE FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE} WHERE vehicleVinCode = :vin")
	suspend fun deleteVehicleFuelHistory(vin: String)
	@Query("DELETE FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE} WHERE vehicleVinCode = :vin")
	suspend fun deleteVehicleMaintenanceHistory(vin: String)
	
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.VEHICLES_TABLE}")
	suspend fun clearAll()
	
}