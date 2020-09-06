/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.09.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entities used to been stored in database
 */

@Entity
data class FuelPriceEntity(
	val fuelStationId: String,
	val price: Double,
	val type: Int
) {
	@PrimaryKey
	var priceId: String = fuelStationId + "_$type"
}