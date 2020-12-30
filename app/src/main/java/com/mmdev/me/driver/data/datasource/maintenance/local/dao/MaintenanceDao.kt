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

package com.mmdev.me.driver.data.datasource.maintenance.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.MaintenanceEntity

/**
 * Dao interface responsible to retrieve cached replaced vehicle spare parts from database
 *
 * Primary used in [com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource]
 */

@Dao
interface MaintenanceDao {
	
	@Query(
		"""
		SELECT * FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		AND systemNode = :systemNode
		AND systemNodeComponent = :component
		ORDER BY date DESC
		LIMIT 1
	"""
	)
	suspend fun findLastReplaced(
		vin: String,
		systemNode: String,
		component: String
	): MaintenanceEntity?
	
	
	
	
	@Query(
		"""
		SELECT * FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		ORDER BY date DESC
		LIMIT :limit OFFSET :offset
	"""
	)
	suspend fun getMaintenanceHistory(
		vin: String,
		limit: Int,
		offset: Int
	): List<MaintenanceEntity>
	
	
	
	
	@Query(
		"""
		SELECT * FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		AND systemNode = :systemNode
		ORDER BY date DESC
	"""
	)
	suspend fun getSystemNodeHistory(
		vin: String,
		systemNode: String
	): List<MaintenanceEntity>
	
	
	/**
	 * @param typedQuery should be in format "%typedQuery%"
	 * this one transformation applied in datasource
	 */
	@Query(
		"""
		SELECT * FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		AND (searchCriteria LIKE :typedQuery
		OR systemNodeComponent LIKE :typedQuery
		OR articulus LIKE :typedQuery
		OR vendor LIKE :typedQuery)
		ORDER BY date DESC
	"""
	)
	suspend fun getByTypedQuery(
		vin: String,
		typedQuery: String
	): List<MaintenanceEntity>
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE} WHERE dateAdded = :key")
	suspend fun getById(key: Long): MaintenanceEntity?
	
	
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertVehicleReplacedSparePart(replacedSpareParts: List<MaintenanceEntity>)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE} WHERE dateAdded = :id")
	suspend fun deleteVehicleReplacedSparePart(id: Long)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}")
	suspend fun clearHistory()
}