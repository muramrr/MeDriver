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

package com.mmdev.me.driver.data.datasource.vehicle.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 *
 */

@Entity(tableName = MeDriverRoomDatabase.VEHICLES_TABLE)
data class VehicleEntity(
	val brand: String,
	val model: String,
	val year: Int,
	@PrimaryKey
	val vin: String,
	@Embedded(prefix = "odometer_current_")
	val odometerValueBound: DistanceBound,
	val engineCapacity: Double,
	@Embedded
	val maintenanceRegulations: MaintenanceRegulationsEntity,
	val lastRefillDate: String,
	val lastUpdatedDate: Long
)