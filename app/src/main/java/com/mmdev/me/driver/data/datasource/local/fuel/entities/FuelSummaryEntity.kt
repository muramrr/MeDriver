/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 15:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 */
@Entity(tableName = "fuel_summary")
data class FuelSummaryEntity(
	val type: Int,
	val minPrice: String,
	val maxPrice: String,
	val avgPrice: String,
	val updatedDate: String
) {
	@PrimaryKey
	val id: String = updatedDate+"_$type"
}