/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.prices.data

/**
 * Domain model used to display in FuelFragment_Prices section
 */

data class FuelStationWithPrices (
	val fuelStation: FuelStation = FuelStation(),
	val prices: HashSet<FuelPrice> = hashSetOf()
)