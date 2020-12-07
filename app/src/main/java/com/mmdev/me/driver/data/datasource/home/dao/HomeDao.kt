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

package com.mmdev.me.driver.data.datasource.home.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.home.entity.VehicleWithExpenses
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.vehicle.data.Expenses

/**
 *
 */

@Dao
interface HomeDao {
	
	@Transaction
	suspend fun getAllVehiclesWithExpenses(): List<VehicleWithExpenses> {
		return getAllVehicles().map {
			VehicleWithExpenses(it, getExpenses(it.vin))
		}
	}
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.VEHICLES_TABLE}")
	suspend fun getAllVehicles(): List<VehicleEntity>
	
	
	@Transaction
	suspend fun getExpenses(vin: String): Expenses {
		return Expenses(
			getExpensesMaintenance(vin) ?: 0.0,
			getExpensesFuelHistory(vin) ?: 0.0
		)
	}
	@Query(
		"""
		SELECT SUM(moneySpent) FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		"""
	)
	suspend fun getExpensesFuelHistory(vin: String): Double?
	@Query(
		"""
		SELECT SUM(moneySpent) FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		"""
	)
	suspend fun getExpensesMaintenance(vin: String): Double?
	
	
	
	@Transaction
	suspend fun getExpensesBetweenTime(start: Long, end: Long): Expenses {
		return Expenses(
			getExpensesMaintenanceBetweenDates(start, end) ?: 0.0,
			getExpensesFuelHistoryBetweenDates(start, end) ?: 0.0
		)
	}
	@Query(
		"""
		SELECT SUM(moneySpent) FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE}
		WHERE date BETWEEN :start AND :end
		"""
	)
	suspend fun getExpensesFuelHistoryBetweenDates(start: Long, end: Long): Double?
	@Query(
		"""
		SELECT SUM(moneySpent) FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE date BETWEEN :start AND :end
		"""
	)
	suspend fun getExpensesMaintenanceBetweenDates(start: Long, end: Long): Double?
	
}