/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.prices.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Combination of fuel provider entity and list of price associated with that provider
 */

data class FuelStationAndPrices(
	@Embedded val fuelStation: FuelStationEntity,
	@Relation(
		parentColumn = "slug",
		entityColumn = "fuelStationId"
	)
	val prices: List<FuelPriceEntity>
)