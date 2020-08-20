/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.08.2020 17:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.prices.model

/**
 * Domain model used to display in FuelFragment_Prices section
 */

data class FuelStationWithPrices (
	val fuelStation: FuelStation = FuelStation(),
	val prices: HashSet<FuelPrice> = hashSetOf()
)