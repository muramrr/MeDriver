/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.11.2020 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase

/**
 * Entity used to been stored in database
 */

@Entity(tableName = MeDriverRoomDatabase.FUEL_STATIONS_TABLE)
data class FuelStationEntity(
	val brandTitle: String,
	val slug: String,
	val updatedDate: String,
	val regionId: Int,
) {
	@PrimaryKey
	var id: String = slug + "_$regionId"
}
