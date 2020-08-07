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

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 *
 */

@Entity(tableName = "fuel_providers")
data class FuelProviderEntity(
	val brandTitle: String,
	@PrimaryKey
	val slug: String,
	val updatedDate: String
)

@Entity
data class FuelPriceEntity(
	val fuelProviderId: String,
	val price: String,
	val type: Int
) {
	@PrimaryKey
	val id: String = fuelProviderId+"_$type"
}


data class FuelProviderAndPrices(
	@Embedded val fuelProvider: FuelProviderEntity,
	@Relation(
			parentColumn = "slug",
			entityColumn = "fuelProviderId"
	)
	val prices: List<FuelPriceEntity>
)