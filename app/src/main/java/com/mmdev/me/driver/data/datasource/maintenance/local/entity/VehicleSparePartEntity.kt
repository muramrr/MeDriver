/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 17:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
 */

@Entity(tableName = MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE)
data class VehicleSparePartEntity (
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