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

package com.mmdev.me.driver.data.datasource.maintenance.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Used to separate data class dependence between data and domain layers
 * Used only to represent data stored in local database
 *
 * @see com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
 *
 * @param systemNode is converted to string enum
 * @see com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
 *
 * @param systemNodeComponent is converted to string child of system node
 * @param searchCriteria is a list converted to a single string (contains component translations)
 */

@Entity(tableName = MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE)
data class MaintenanceEntity (
	val date: Long,
	@PrimaryKey
	val dateAdded: Long,
	val articulus: String = "",
	val vendor: String = "",
	val systemNode: String,
	val systemNodeComponent: String,
	val searchCriteria: String,
	val commentary: String = "",
	val moneySpent: Double,
	@Embedded(prefix = "replaced_part_")
	val odometerValueBound: DistanceBound,
	val vehicleVinCode: String
)