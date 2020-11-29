/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.11.2020 00:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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