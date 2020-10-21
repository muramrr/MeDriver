/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.10.2020 18:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity

/**
 * Dao interface responsible to retrieve cached replaced vehicle spare parts from database
 *
 * Primary used in [com.mmdev.me.driver.data.datasource.maintenance.local]
 */

@Dao
interface MaintenanceDao {
	
	@Query(
		"""
		SELECT * FROM vehicle_replaced_parts
		WHERE vehicleVinCode = :vin
		AND systemNode = :systemNode
		AND customNodeComponent = :component
		ORDER BY date DESC
		LIMIT 1
	"""
	)
	suspend fun findLastReplaced(
		vin: String,
		systemNode: String,
		component: String
	): VehicleSparePartEntity
	
	
	
	
	@Query(
		"""
		SELECT * FROM vehicle_replaced_parts
		WHERE vehicleVinCode = :vin
		ORDER BY date DESC
		LIMIT :limit OFFSET :offset
	"""
	)
	suspend fun getMaintenanceHistory(
		vin: String,
		limit: Int,
		offset: Int
	): List<VehicleSparePartEntity>
	
	
	
	
	@Query(
		"""
		SELECT * FROM vehicle_replaced_parts
		WHERE vehicleVinCode = :vin
		AND systemNode = :systemNode
		ORDER BY date DESC
	"""
	)
	suspend fun getSystemNodeHistory(
		vin: String,
		systemNode: String
	): List<VehicleSparePartEntity>
	
	
	
	
	@Query(
		"""
		SELECT * FROM vehicle_replaced_parts
		WHERE vehicleVinCode = :vin
		AND systemNode LIKE :typedQuery
		OR systemNodeComponent LIKE :typedQuery
		OR articulus LIKE :typedQuery
		OR vendor LIKE :typedQuery
		ORDER BY date DESC
	"""
	)
	suspend fun getByTypedQuery(
		vin: String,
		typedQuery: String //should be in format "%typedQuery%"
	): List<VehicleSparePartEntity>
	
	
	
	
	
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertVehicleReplacedSparePart(replacedSpareParts: List<VehicleSparePartEntity>)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun updateVehicleReplacedSparePart(replacedSparePart: VehicleSparePartEntity)
	
	@Delete
	suspend fun deleteVehicleReplacedSparePart(replacedSparePart: VehicleSparePartEntity)
	
	@Query("DELETE FROM vehicle_replaced_parts")
	suspend fun clearHistory()
}