/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 20:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Combination of fuel provider entity and list of price associated with that provider
 */

data class FuelProviderAndPrices(
	@Embedded val fuelProvider: FuelProviderEntity,
	@Relation(
		parentColumn = "slug",
		entityColumn = "fuelProviderId"
	)
	val prices: List<FuelPriceEntity>
)